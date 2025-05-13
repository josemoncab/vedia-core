package com.vediastudios.vediacore;

import com.vediastudios.vediacore.configurations.FilesManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public abstract class VediaPlugin extends JavaPlugin {
    private static VediaPlugin instance;

    private FilesManager filesManager;

    public static VediaPlugin getInstance() {
        if (instance == null) {
            instance = JavaPlugin.getPlugin(VediaPlugin.class);

            Objects.requireNonNull(instance, "Cannot create a new instance!");
        }

        return instance;
    }

    @Override
    public void onLoad() {
        getInstance();

        this.onPluginLoad();
    }

    @Override
    public void onEnable() {
        filesManager = FilesManager.getInstance();

        onPluginStart();
    }

    @Override
    public void onDisable() {
        this.filesManager.stop();

        this.onPluginShutdown();
    }

    /**
     * Run when the plugins load. At this point the paper api is not available
     */
    protected void onPluginLoad() {
    }

    /**
     * Run when the plugin is enabling.
     */
    protected abstract void onPluginStart();

    /**
     * Run when the plugin unloads
     */
    protected abstract void onPluginShutdown();

    public File getFile() {
        return super.getFile();
    }

    public FilesManager getFilesManager() {
        return filesManager;
    }
}
