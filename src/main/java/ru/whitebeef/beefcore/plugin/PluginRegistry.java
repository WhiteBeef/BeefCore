package ru.whitebeef.beefcore.plugin;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.whitebeef.beefcore.BeefCore;
import ru.whitebeef.beefcore.exceptions.plugin.PluginAlreadyLoadedException;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Log4j2
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class PluginRegistry {

    protected final Map<String, Plugin> plugins = new HashMap<>();
    protected final Map<Class<? extends BeefPlugin>, Plugin> classToPlugins = new HashMap<>();
    protected final Map<String, Class<?>> classes = new HashMap<>();
    private final Map<String, PluginClassLoader> pluginsToLoad = new HashMap<>();
    private final BeefCore beefCore;
    private final AnnotationConfigApplicationContext applicationContext;

    @Autowired
    public PluginRegistry(BeefCore beefCore, AnnotationConfigApplicationContext applicationContext) {
        this.beefCore = beefCore;
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    private void loadPluginsFromDefaultFolder() {
        registerPlugins("plugins");
        enablePlugins();
    }


    public void loadPlugin(PluginClassLoader pluginClassLoader) {
        try {
            PluginInfo info = pluginClassLoader.getInfo();
            String lowerPluginName = info.getName().toLowerCase();
            if (plugins.containsKey(lowerPluginName)) {
                throw new PluginAlreadyLoadedException(info);
            }

            AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
            applicationContext.setClassLoader(pluginClassLoader);
            applicationContext.setParent(this.applicationContext);
            String basePackage = info.getMainClassPath().substring(0, info.getMainClassPath().lastIndexOf('.'));
            StringBuilder beanName = new StringBuilder(info.getName());
            beanName.setCharAt(0, Character.toLowerCase(beanName.charAt(0)));
            Class<? extends BeefPlugin> pluginMainClass = (Class<? extends BeefPlugin>) pluginClassLoader.findClass(info.getMainClassPath());
            applicationContext.registerBean(pluginMainClass, info, pluginClassLoader, applicationContext);
            applicationContext.scan(basePackage);

            for (String parentName : info.getDepends()) {
                if (!plugins.containsKey(parentName.toLowerCase())) {
                    loadPlugin(pluginsToLoad.get(parentName));
                }
                Plugin parentPlugin = plugins.get(parentName.toLowerCase());
                BeanDefinitionRegistry parentDefinitionRegistry = parentPlugin.getPluginApplicationContext();
                BeanDefinitionRegistry childBeanDefinitionRegistry = ((BeanDefinitionRegistry) applicationContext.getBeanFactory());
                String[] beanDefinitionNames = parentDefinitionRegistry.getBeanDefinitionNames();
                for (String bean : beanDefinitionNames) {
                    if (bean.startsWith("org.springframework.context")) {
                        continue;
                    }
                    BeanDefinition beanDefinition = parentDefinitionRegistry.getBeanDefinition(bean);

                    if (beanDefinition.getBeanClassName() == null) {
                        continue;
                    }
                    Field resolvableDependenciesField = DefaultListableBeanFactory.class.getDeclaredField("resolvableDependencies");
                    Field beanDefinitionNamesField = DefaultListableBeanFactory.class.getDeclaredField("beanDefinitionNames");
                    Field singletonBeanNamesByTypeField = DefaultListableBeanFactory.class.getDeclaredField("singletonBeanNamesByType");
                    Field allBeanNamesByTypeField = DefaultListableBeanFactory.class.getDeclaredField("allBeanNamesByType");
                    Field beanDefinitionMapField = DefaultListableBeanFactory.class.getDeclaredField("beanDefinitionMap");

                    resolvableDependenciesField.setAccessible(true);
                    beanDefinitionNamesField.setAccessible(true);
                    singletonBeanNamesByTypeField.setAccessible(true);
                    allBeanNamesByTypeField.setAccessible(true);
                    beanDefinitionMapField.setAccessible(true);

                    Map<Class<?>, Object> resolvableDependenciesMap = (Map<Class<?>, Object>) resolvableDependenciesField.get(childBeanDefinitionRegistry);
                    Map<Class<?>, String[]> singletonBeanNamesByTypeMap = (Map<Class<?>, String[]>) singletonBeanNamesByTypeField.get(childBeanDefinitionRegistry);
                    Map<Class<?>, String[]> allBeanNamesByTypeMap = (Map<Class<?>, String[]>) allBeanNamesByTypeField.get(childBeanDefinitionRegistry);
                    Map<String, BeanDefinition> beanDefinitionMap = (Map<String, BeanDefinition>) beanDefinitionMapField.get(childBeanDefinitionRegistry);
                    List<String> list = (List<String>) beanDefinitionNamesField.get(childBeanDefinitionRegistry);

                    Class<?> clazz = ((PluginClassLoader) parentPlugin.getClass().getClassLoader()).findClass(beanDefinition.getBeanClassName());
                    GenericApplicationContext parentApplicationContext = plugins.get(parentName.toLowerCase()).getPluginApplicationContext();
                    String parentBeanName = parentApplicationContext.getBeanNamesForType(clazz)[0];

                    resolvableDependenciesMap.put(clazz, parentApplicationContext.getBean(clazz));
                    singletonBeanNamesByTypeMap.put(clazz, new String[]{parentBeanName});
                    allBeanNamesByTypeMap.put(clazz, new String[]{parentBeanName});
                    beanDefinitionMap.put(parentBeanName, beanDefinition);
                    list.add(parentBeanName);


                    Field singletonObjectsField = DefaultSingletonBeanRegistry.class.getDeclaredField("singletonObjects");
                    Field containedBeanMapField = DefaultSingletonBeanRegistry.class.getDeclaredField("containedBeanMap");

                    singletonObjectsField.setAccessible(true);
                    containedBeanMapField.setAccessible(true);

                    Map<String, Object> singletonObjectsMap = (Map<String, Object>) singletonObjectsField.get(childBeanDefinitionRegistry);
                    Map<String, Set<String>> stringSetMap = (Map<String, Set<String>>) containedBeanMapField.get(childBeanDefinitionRegistry);

                    singletonObjectsMap.put(parentBeanName, parentApplicationContext.getBean(clazz));
                    stringSetMap.put(parentBeanName, Set.of(parentBeanName));
                }

            }

            applicationContext.refresh();

            Plugin plugin = (Plugin) applicationContext.getBean(beanName.toString());

            plugins.put(lowerPluginName, plugin);
            classToPlugins.put(pluginMainClass, plugin);
            plugin.onLoad();

            if (plugin instanceof BeefPlugin beefPlugin) {
                beefPlugin.saveDefaultConfig();
            }
        } catch (Exception exception) {
            log.error("Ошибка при загрузке плагина " + pluginClassLoader.getInfo().getName() + "!");
            exception.printStackTrace();
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

    @Nullable
    public Plugin getPlugin(Class<? extends BeefPlugin> clazz) {
        return classToPlugins.get(clazz);
    }

    public void registerPlugins(String folder) {
        try {
            Path pluginsFolderPath = Path.of(beefCore.getMainFolder() + File.separator + folder);
            if (!Files.isDirectory(pluginsFolderPath)) {
                Files.createDirectories(pluginsFolderPath);
            }

            File[] files = pluginsFolderPath.toFile().listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"));


            for (File file : files) {
                try {
                    PluginClassLoader pluginClassLoader = new PluginClassLoader(this, file.toPath());
                    pluginsToLoad.put(pluginClassLoader.getInfo().getName(), pluginClassLoader);
                } catch (Exception exception) {
                    log.error("Ошибка во время регистрации плагина: " + file.getName());
                    exception.printStackTrace();
                }
            }
            for (PluginClassLoader pluginClassLoader : new ArrayList<>(pluginsToLoad.values())) {
                if (!plugins.containsKey(pluginClassLoader.getInfo().getName().toLowerCase())) {
                    loadPlugin(pluginClassLoader);
                }
            }


        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }


    public void enablePlugins() {
        for (Plugin plugin : plugins.values()) {
            if (!plugin.isEnabled()) {
                enablePlugin(plugin, new HashSet<>());
            }
        }
    }

    private boolean enablePlugin(Plugin plugin, Set<String> used) {
        PluginInfo info = plugin.getInfo();
        used.add(info.getName());
        boolean dependsOn = true;

        for (String pluginName : info.getDepends()) {
            Plugin pl = plugins.get(pluginName.toLowerCase());
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
