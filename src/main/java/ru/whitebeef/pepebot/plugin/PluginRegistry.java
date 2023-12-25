package ru.whitebeef.pepebot.plugin;

import org.jetbrains.annotations.Nullable;
import ru.whitebeef.pepebot.PepeBot;
import ru.whitebeef.pepebot.exceptions.plugin.PluginAlreadyLoadedException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class PluginRegistry {

    protected final Map<String, Plugin> plugins = new HashMap<>();
    protected final Map<String, Class<?>> classes = new HashMap<>();

    public void loadPlugin(PluginClassLoader pluginClassLoader) throws PluginAlreadyLoadedException {
        PluginInfo info = pluginClassLoader.getInfo();
        String lowerPluginName = info.getName().toLowerCase();
        if (plugins.containsKey(lowerPluginName)) {
            throw new PluginAlreadyLoadedException(info);
        }
        Plugin plugin = null;
        try {
            plugin = (Plugin) Class.forName(info.getMainClassPath()).getConstructor(PluginInfo.class, PluginClassLoader.class).newInstance(info, pluginClassLoader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        plugins.put(lowerPluginName, plugin);
        plugin.onLoad();
    }

    @Nullable
    public Plugin getPlugin(String name) {
        return plugins.get(name);
    }


    public void registerPlugins(String folder) {
        try {
            Path pluginsFolderPath = Path.of(PepeBot.getInstance().getMainFolder() + File.separator + folder);
            if (!Files.isDirectory(pluginsFolderPath)) {
                return;
            }
            if (!pluginsFolderPath.toFile().exists()) {
                pluginsFolderPath.toFile().createNewFile();
            }

            File[] files = pluginsFolderPath.toFile().listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"));

            for (File file : files) {
                PluginClassLoader pluginClassLoader = new PluginClassLoader(this, file.toPath());
                loadPlugin(pluginClassLoader);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
