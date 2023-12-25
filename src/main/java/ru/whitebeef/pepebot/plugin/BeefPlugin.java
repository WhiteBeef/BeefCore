package ru.whitebeef.pepebot.plugin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.whitebeef.pepebot.PepeBot;
import ru.whitebeef.pepebot.utils.GsonUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.jar.JarFile;

public abstract class BeefPlugin implements Plugin {

    @NotNull
    @Getter
    private final PluginInfo info;
    @Getter
    @NotNull
    private final File dataFolder;

    @Getter
    private final PluginClassLoader pluginClassLoader;


    public BeefPlugin(@NotNull PluginInfo info, PluginClassLoader pluginClassLoader) {
        this.info = info;
        this.dataFolder = new File(PepeBot.class.getProtectionDomain().getCodeSource().getLocation().toString()
                + "plugins" + File.separator + info.getName() + File.separator);
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
            return this.pluginClassLoader.saveResource(Path.of(dataFolder + "config.json"), true);
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public @NotNull JsonElement getConfig() {
        try {
            return GsonUtils.getJsonObject(new File(dataFolder.getPath() + File.separator + "config.json"));
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

    public boolean getResources(@NotNull Path path, boolean deep) throws IOException {
        return this.pluginClassLoader.saveResource(path, deep);
    }
}
