package com.vediastudios.vediacore.configurations;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Data of a loaded configuration
 */
public class ConfigurationHolder {

    private final File file;
    private final YamlConfiguration yamlConfiguration;

    /**
     * Create a new holder with the data
     *
     * @param file              Associated {@link File}
     * @param yamlConfiguration Associeted {@link YamlConfiguration}
     */
    public ConfigurationHolder(File file, YamlConfiguration yamlConfiguration) {
        this.file = file;
        this.yamlConfiguration = yamlConfiguration;
    }

    /**
     * Save the configuration to the file
     */
    public void save() {
        try {
            yamlConfiguration.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the configuration
     *
     * @return {@link YamlConfiguration} instance
     */
    public YamlConfiguration getYamlConfiguration() {
        return yamlConfiguration;
    }
}
