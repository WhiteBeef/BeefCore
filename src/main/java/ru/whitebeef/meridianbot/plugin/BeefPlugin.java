package ru.whitebeef.meridianbot.plugin;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
import ru.whitebeef.meridianbot.utils.GsonUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Getter
public abstract class BeefPlugin implements Plugin {

    @NotNull
    private final PluginInfo info;
    @NotNull
    private final File dataFolder;

    private final PluginClassLoader pluginClassLoader;
    private final ApplicationContext pluginApplicationContext;
    @Setter
    private boolean enabled;

    public BeefPlugin(@NotNull PluginInfo info, @NotNull PluginClassLoader pluginClassLoader, @NotNull ApplicationContext pluginApplicationContext) {
        this.info = info;
        this.dataFolder = new File(pluginClassLoader.getDataFolderPath().toAbsolutePath() + "/");
        this.pluginApplicationContext = pluginApplicationContext;
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        this.pluginClassLoader = pluginClassLoader;
    }


    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    @NotNull
    public JarFile getJarFile() {
        return pluginClassLoader.getJarFile();
    }

    public boolean saveDefaultConfig(boolean forceReplace) {
        try {
            if (!forceReplace && new File(this.dataFolder.getAbsolutePath() + "/config.json").exists()) {
                return true;
            }
            JarEntry configJarEntry = getJarFile().getJarEntry("config.json");
            if (configJarEntry == null) {
                return false;
            }
            FileCopyUtils.copy(getJarFile().getInputStream(configJarEntry), new FileOutputStream(this.dataFolder.getAbsolutePath() + "/config.json"));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean saveDefaultConfig() {
        return saveDefaultConfig(false);
    }

    @Override
    public @NotNull JsonObject getConfig() {
        try {
            return GsonUtils.getJsonObject(ResourceUtils.getFile("file:" + dataFolder.getAbsolutePath() + "/config.json")).getAsJsonObject();
        } catch (Exception ignored) {
        }
        return new JsonObject();
    }

    protected boolean saveResource(@NotNull Path from, boolean replace) throws IOException {
        return this.saveResource(from, this.dataFolder.toPath().resolve(from), replace);
    }

    protected boolean saveResource(@NotNull Path from, @NotNull Path to, boolean replace) throws IOException {
        return this.pluginClassLoader.saveResource(from, to, replace);
    }

    public boolean saveResources(@NotNull Path path, boolean deep) throws IOException {
        return this.pluginClassLoader.saveResource(path, deep);
    }

    public List<Path> getResources(@NotNull Path path, boolean deep) {
        return this.pluginClassLoader.getResources(path, deep);
    }


}
