package ru.whitebeef.pepebot.exceptions.plugin;

import ru.whitebeef.pepebot.plugin.PluginInfo;

public class PluginAlreadyLoadedException extends Exception {

    public PluginAlreadyLoadedException(PluginInfo info) {
        super("Plugin " + info.getName() + " is already loaded!");
    }
}
