package com.vediastudios.vediacore.configurations;

import com.vediastudios.vediacore.VediaPlugin;
import com.vediastudios.vediacore.configurations.annotations.LanguageSource;
import com.vediastudios.vediacore.configurations.annotations.Source;
import com.vediastudios.vediacore.logger.Log;
import com.vediastudios.vediacore.utils.ReflectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Manager of the configurations system
 */
public final class FilesManager {

    private List<Class<?>> valuesHolders;

    private static FilesManager INSTANCE;

    /**
     * Map with the valie of {@link Source} or {@link LanguageSource} and its corresponded {@link ConfigurationHolder}
     */
    public static Map<String, ConfigurationHolder> configs = new HashMap<>();

    private FilesManager() {
        Log.info("Start loading files...");
        long start = System.currentTimeMillis();

        if (!VediaPlugin.getInstance().getDataFolder().exists()) {
            VediaPlugin.getInstance().getDataFolder().mkdirs();
        }

        valuesHolders = ReflectionUtils.getClasses();

        this.load();
        Log.info("Time took to load files: " + (System.currentTimeMillis() - start) + "ms");
    }

    /**
     * Reloads all configurations
     */
    public void reload() {
        this.saveAll();
        this.load();
    }

    /**
     * Run when the server stops
     */
    public void stop() {
        this.saveAll();
    }

    /**
     * Save all configurations
     * */
    private void saveAll() {
        configs.forEach((k, v) -> v.save());
    }

    private void load() {
        AtomicReference<Class<?>> langClass = new AtomicReference<>();
        valuesHolders.forEach(clazz -> {
            if (clazz.isAnnotationPresent(Source.class)) {
                configs.put(clazz.getAnnotation(Source.class).value().split("\\.")[0], ConfigurationFactory.load(clazz));
            }

            if (clazz.isAnnotationPresent(LanguageSource.class)) {
                langClass.set(clazz);
            }
        });

        configs.put("lang", ConfigurationFactory.load(langClass.get()));

    }

    /**
     * Global instance of the {@link FilesManager}
     * @return Singleton instance
     * */
    public static FilesManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FilesManager();
        }
        return INSTANCE;
    }
}