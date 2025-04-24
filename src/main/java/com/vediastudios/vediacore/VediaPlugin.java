package com.vediastudios.vediacore;

import com.vediastudios.vediacore.configuration.FilesManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public abstract class VediaPlugin extends JavaPlugin {
    private static VediaPlugin instance;

    private FilesManager fm;

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
        fm = new FilesManager();
        fm.init();

        onPluginStart();
    }

    @Override
    public void onDisable() {
        fm.stop();

        this.onPluginShutdown();
    }

    protected void onPluginLoad() {
    }

    protected abstract void onPluginStart();

    protected abstract void onPluginShutdown();

    public File getFile() {
        return super.getFile();
    }
}
