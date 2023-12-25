package ru.whitebeef.pepebot.plugin;

import org.jetbrains.annotations.NotNull;

public interface PluginInfo {

    @NotNull
    String getName();

    @NotNull
    String getMainClassPath();

    @NotNull
    String[] getDepends();

    @NotNull
    String[] getSoftDepends();

}
