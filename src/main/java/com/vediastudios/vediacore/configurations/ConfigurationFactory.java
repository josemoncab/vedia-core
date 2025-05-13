package com.vediastudios.vediacore.configurations;

import com.vediastudios.vediacore.VediaPlugin;
import com.vediastudios.vediacore.configurations.annotations.*;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Creates a configuration from a class
 */
public final class ConfigurationFactory {

    /**
     * Load the configuration in the class
     *
     * @param clazz The class to load
     * @return A new {@link ConfigurationHolder} of the class
     */
    public static ConfigurationHolder load(Class<?> clazz) {
        String path;

        if (clazz.isAnnotationPresent(Source.class)) {
            path = clazz.getAnnotation(Source.class).value();
        } else {
            path = clazz.getAnnotation(LanguageSource.class).value();

            if (path != FilesManager.configs.get("settings").getYamlConfiguration().getString("Lang")) {
                path = "lang/" + FilesManager.configs.get("settings").getYamlConfiguration().getString("Lang");
            }
        }

        if (!path.endsWith(".yml")) {
            path += ".yml";
        }

        File file = new File(VediaPlugin.getInstance().getDataFolder(), path);
        YamlConfiguration yamlConfiguration = new YamlConfiguration();

        if (file.exists()) {
            return new ConfigurationHolder(file, updateFile(file, clazz));

        } else {
            classToConfig(yamlConfiguration, clazz, "");
            try {
                yamlConfiguration.save(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return new ConfigurationHolder(file, yamlConfiguration);
        }
    }

    private static YamlConfiguration updateFile(File file, Class<?> clazz) {
        YamlConfiguration userConfig = YamlConfiguration.loadConfiguration(file);
        YamlConfiguration oriConfig = new YamlConfiguration();
        classToConfig(oriConfig, clazz, "");

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

        return userConfig;
    }

    private static void classToConfig(YamlConfiguration config, Class<?> clazz, String path) {
        if (clazz.isAnnotationPresent(Comments.class)) {
            config.setComments("", List.of(clazz.getAnnotation(Comments.class).value()));
        }

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (Modifier.isTransient(field.getModifiers())) {
                continue;
            }

            field.setAccessible(true);

            if (field.getType().getName().startsWith(clazz.getPackageName())) {
                Class<?> innerClass = field.getType();
                if (innerClass.isAnnotationPresent(Section.class)) {
                    classToConfig(config, innerClass, path + innerClass.getAnnotation(Section.class).value() + ".");
                }
            }

            if (!field.isAnnotationPresent(Path.class)) {
                continue;
            }

            String configPath = path + String.join(".", field.getAnnotation(Path.class).value());

            try {
                config.set(configPath, field.get(null));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            if (field.isAnnotationPresent(Comments.class)) {
                config.setComments(configPath, List.of(field.getAnnotation(Comments.class).value()));
            }
        }
    }

    private static void configToClass(YamlConfiguration config, Class<?> clazz, String path) {
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (Modifier.isTransient(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
                continue;
            }

            field.setAccessible(true);

            if (field.getType().getName().startsWith(clazz.getPackageName())) {
                Class<?> innerClass = field.getType();
                if (innerClass.isAnnotationPresent(Section.class)) {
                    configToClass(config, innerClass, path + innerClass.getAnnotation(Section.class).value() + ".");
                }
            }

            if (!field.isAnnotationPresent(Path.class)) {
                continue;
            }

            String configPath = path + String.join(".", field.getAnnotation(Path.class).value());

            try {
                field.set(null, config.get(configPath));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private ConfigurationFactory() {
    }
}
