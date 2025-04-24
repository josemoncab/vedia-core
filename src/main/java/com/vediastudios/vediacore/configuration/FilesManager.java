package com.vediastudios.vediacore.configuration;

import com.vediastudios.vediacore.VediaPlugin;
import com.vediastudios.vediacore.annotations.*;
import com.vediastudios.vediacore.logger.Log;
import com.vediastudios.vediacore.utils.ReflectionUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Manager of the configuration system
 */
public final class FilesManager {

    private List<Class<?>> valuesHolders;
    private HashMap<String, YamlConfiguration> configs = new HashMap<>();

    public void init() {
        Log.info("Start loading files...");
        long start = System.currentTimeMillis();

        if (!VediaPlugin.getInstance().getDataFolder().exists()) {
            VediaPlugin.getInstance().getDataFolder().mkdirs();
        }

        valuesHolders = ReflectionUtils.getClasses();

        this.load();
        Log.info("Time took to load files: " + (System.currentTimeMillis() - start) + "ms");
    }

    public void reload() {
        this.saveAll();
        this.load();
    }

    public void stop() {
        this.saveAll();
    }

    private void saveAll() {

    }

    private void load() {
        Class<? extends Annotation> configHolderA = ConfigurationHolder.class;
        Class<? extends Annotation> langHolderA = LanguageHolder.class;

        AtomicReference<Class<?>> langClass = new AtomicReference<>();
        valuesHolders.forEach(clazz -> {
            if (clazz.isAnnotationPresent(configHolderA)) {
                this.loadSettings(clazz);
            }

            if (clazz.isAnnotationPresent(langHolderA)) {
                langClass.set(clazz);
            }
        });

        this.loadLang(langClass.get());

    }

    private void loadSettings(Class<?> clazz) {
        String fileName = clazz.getAnnotation(ConfigurationHolder.class).value();
        if (!fileName.endsWith(".yml")) {
            fileName += ".yml";
        }

        Path filePath = Path.of(VediaPlugin.getInstance().getDataFolder().getPath(), fileName);
        File file = filePath.toFile();

        if (!file.exists()) {
            this.generateFile(file, clazz);
        } else {
            this.updateFile(file, clazz);
        }
    }

    private void loadLang(Class<?> clazz) {
        // TODO: Search for the file in the jar and fallback to en_us if not found
        String fileName = clazz.getAnnotation(LanguageHolder.class).value();

        if (fileName != configs.get("settings.yml").getString("Lang")) {
            fileName = configs.get("settings.yml").getString("Lang");
        }

        if (!fileName.endsWith(".yml")) {
            fileName += ".yml";
        }

        Path filePath = Path.of(VediaPlugin.getInstance().getDataFolder().getPath(), "lang", fileName);
        File file = filePath.toFile();

        if (!file.exists()) {
            this.generateFile(file, clazz);
        } else {
            this.updateFile(file, clazz);
        }
    }

    private void generateFile(File file, Class<?> clazz) {
        YamlConfiguration config = new YamlConfiguration();

        this.classToConfig(config, clazz, "");
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        configs.put(file.getName(), config);
    }

    private void updateFile(File file, Class<?> clazz) {
        YamlConfiguration userConfig = YamlConfiguration.loadConfiguration(file);
        YamlConfiguration oriConfig = new YamlConfiguration();
        this.classToConfig(oriConfig, clazz, "");

        boolean modified = false;
        for (String userKey : userConfig.getKeys(true)) {
            if (!oriConfig.contains(userKey)) {
                userConfig.set(userKey, null);
                modified = true;
            }
        }

        for (String oriKey : oriConfig.getKeys(true)) {
            if (!userConfig.contains(oriKey)) {
                userConfig.set(oriKey, oriConfig.get(oriKey));
                userConfig.setComments(oriKey, oriConfig.getComments(oriKey));
                modified = true;
            }
        }

        if (modified) {
            try {
                userConfig.save(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        configToClass(userConfig, clazz, "");

        configs.put(file.getName(), userConfig);
    }

    private void classToConfig(YamlConfiguration config, Class<?> clazz, String path) {
        if (clazz.isAnnotationPresent(ConfigComment.class)) {
            config.setComments("", List.of(clazz.getAnnotation(ConfigComment.class).value()));
        }

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (Modifier.isTransient(field.getModifiers())) {
                continue;
            }

            field.setAccessible(true);

            if (field.getType().getName().startsWith(clazz.getPackageName())) {
                Class<?> innerClass = field.getType();
                if (innerClass.isAnnotationPresent(ConfigSection.class)) {
                    classToConfig(config, innerClass, path + innerClass.getAnnotation(ConfigSection.class).value() + ".");
                }
            }

            if (!field.isAnnotationPresent(ConfigPath.class)) {
                continue;
            }

            String configPath = path + String.join(".", field.getAnnotation(ConfigPath.class).value());

            try {
                config.set(configPath, field.get(null));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            if (field.isAnnotationPresent(ConfigComment.class)) {
                config.setComments(configPath, List.of(field.getAnnotation(ConfigComment.class).value()));
            }
        }
    }

    private void configToClass(YamlConfiguration config, Class<?> clazz, String path) {
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (Modifier.isTransient(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
                continue;
            }

            field.setAccessible(true);

            if (field.getType().getName().startsWith(clazz.getPackageName())) {
                Class<?> innerClass = field.getType();
                if (innerClass.isAnnotationPresent(ConfigSection.class)) {
                    classToConfig(config, innerClass, path + innerClass.getAnnotation(ConfigSection.class).value() + ".");
                }
            }

            if (!field.isAnnotationPresent(ConfigPath.class)) {
                continue;
            }

            String configPath = path + String.join(".", field.getAnnotation(ConfigPath.class).value());

            try {
                field.set(null, config.get(configPath));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}