package ru.whitebeef.meridianbot.plugin;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import ru.whitebeef.meridianbot.MeridianBot;
import ru.whitebeef.meridianbot.exceptions.plugin.PluginAlreadyLoadedException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Log4j2
@Component
public class PluginRegistry {

    protected final Map<String, Plugin> plugins = new HashMap<>();
    protected final Map<String, Class<?>> classes = new HashMap<>();

    private final MeridianBot meridianBot;
    private final AnnotationConfigApplicationContext applicationContext;


    @Autowired
    public PluginRegistry(MeridianBot meridianBot, AnnotationConfigApplicationContext applicationContext) {
        this.meridianBot = meridianBot;
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    private void loadDefaultFolder() {
        registerPlugins("plugins");
        enablePlugins();
    }

    public void loadPlugin(PluginClassLoader pluginClassLoader) throws PluginAlreadyLoadedException, ClassNotFoundException {
        PluginInfo info = pluginClassLoader.getInfo();
        String lowerPluginName = info.getName().toLowerCase();
        if (plugins.containsKey(lowerPluginName)) {
            throw new PluginAlreadyLoadedException(info);
        }

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.setClassLoader(pluginClassLoader);
        applicationContext.setParent(this.applicationContext);
        applicationContext.scan(info.getMainClassPath().substring(0, info.getMainClassPath().lastIndexOf('.')));
        StringBuilder beanName = new StringBuilder(info.getName());
        beanName.setCharAt(0, Character.toLowerCase(beanName.charAt(0)));
        applicationContext.registerBean(pluginClassLoader.findClass(info.getMainClassPath()), info, pluginClassLoader);


        applicationContext.refresh();
        Plugin plugin = (Plugin) applicationContext.getBean(beanName.toString());

        plugins.put(lowerPluginName, plugin);
        plugin.onLoad();

        if (plugin instanceof BeefPlugin beefPlugin) {
             beefPlugin.saveDefaultConfig();
        }
    }

    @Nullable
    public Plugin getPlugin(String name) {
        return plugins.get(name);
    }

    @NotNull
    public Collection<Plugin> getLoadedPlugins() {
        return plugins.values();
    }

    public void registerPlugins(String folder) {
        try {
            Path pluginsFolderPath = Path.of(meridianBot.getMainFolder() + File.separator + folder);
            if (!Files.isDirectory(pluginsFolderPath)) {
                Files.createDirectories(pluginsFolderPath);
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


    public void enablePlugins() {
        for (Plugin plugin : plugins.values()) {
            enablePlugin(plugin, new HashSet<>());
        }
    }

    private boolean enablePlugin(Plugin plugin, Set<String> used) {
        PluginInfo info = plugin.getInfo();
        used.add(info.getName());
        boolean dependsOn = true;

        for (String pluginName : info.getDepends()) {
            Plugin pl = plugins.get(pluginName);
            if (pl.isEnabled()) {
                continue;
            }
            if (used.contains(pluginName)) {
                throw new RuntimeException("Обнаружена кольцевая зависимость: " + info.getName() + " <-> " + pluginName);
            }
            try {
                enablePlugin(pl, used);
            } catch (Exception e) {
                dependsOn = false;
            }
        }

        if (dependsOn) {
            plugin.onEnable();
            plugin.setEnabled(true);
        } else {
            log.error("Ошибка при загрузки плагина " + plugin.getInfo().getName());
        }
        return true;
    }
}
