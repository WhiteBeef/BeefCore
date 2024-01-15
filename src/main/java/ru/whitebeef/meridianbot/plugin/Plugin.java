package ru.whitebeef.meridianbot.plugin;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.jar.JarFile;

public interface Plugin {

    @NotNull
    PluginInfo getInfo();

    @NotNull
    File getDataFolder();

    @NotNull
    JarFile getJarFile();

    @NotNull
    JsonElement getConfig();

    boolean isEnabled();

    void setEnabled(boolean enable);

    void onLoad();

    void onEnable();

    void onDisable();
}
