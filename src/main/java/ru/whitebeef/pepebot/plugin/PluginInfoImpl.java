package ru.whitebeef.pepebot.plugin;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class PluginInfoImpl implements PluginInfo {

    @NotNull
    @Getter
    private final String name;

    @NotNull
    @Getter
    private final String mainClassPath;

    @NotNull
    @Getter
    private final String[] depends;

    @NotNull
    @Getter
    private final String[] softDepends;


    public PluginInfoImpl(@NotNull String name, @NotNull String mainClassPath, @NotNull String[] depends, @NotNull String[] softDepends) {
        this.name = name;
        this.mainClassPath = mainClassPath;
        this.depends = depends;
        this.softDepends = softDepends;
    }

}
