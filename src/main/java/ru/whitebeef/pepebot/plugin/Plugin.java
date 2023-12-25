package ru.whitebeef.pepebot.plugin;

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

    void onLoad();

    void onEnable();

    void onDisable();
}
