package ru.whitebeef.meridianbot.exceptions.plugin;

import ru.whitebeef.meridianbot.plugin.PluginInfo;

public class PluginAlreadyLoadedException extends Exception {

    public PluginAlreadyLoadedException(PluginInfo info) {
        super("Plugin " + info.getName() + " is already loaded!");
    }
}
