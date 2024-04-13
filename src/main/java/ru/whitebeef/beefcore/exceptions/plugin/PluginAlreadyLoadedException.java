package ru.whitebeef.beefcore.exceptions.plugin;

import ru.whitebeef.beefcore.plugin.PluginInfo;

public class PluginAlreadyLoadedException extends Exception {

    public PluginAlreadyLoadedException(PluginInfo info) {
        super("Plugin " + info.getName() + " is already loaded!");
    }
}
