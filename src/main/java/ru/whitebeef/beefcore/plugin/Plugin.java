package ru.whitebeef.beefcore.plugin;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.support.GenericApplicationContext;

import java.io.File;
import java.util.jar.JarFile;

public interface Plugin {

    @NotNull
    PluginInfo getInfo();

    @NotNull
    File getDataFolder();

    @NotNull
    GenericApplicationContext getPluginApplicationContext();

    @NotNull
    JarFile getJarFile();

    @NotNull
    JsonObject getConfig();

    boolean isEnabled();

    void setEnabled(boolean enable);

    void onLoad();

    void onEnable();

    void onDisable();
}
