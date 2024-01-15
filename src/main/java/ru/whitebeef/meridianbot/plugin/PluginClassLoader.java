package ru.whitebeef.meridianbot.plugin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.whitebeef.meridianbot.MeridianBot;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginClassLoader extends URLClassLoader {

    static {
        ClassLoader.registerAsParallelCapable();
    }

    protected final PluginRegistry registry;
    @Getter
    protected final PluginInfo info;
    @Getter
    protected final Path filePath;
    @Getter
    protected final Path dataFolderPath;
    @Getter
    protected final JarFile jarFile;
    protected final URL url;

    protected final Map<String, Class<?>> classes = new HashMap<>();

    protected PluginClassLoader(@NotNull PluginRegistry registry, @NotNull Path filePath) throws IOException {
        super(new URL[0], MeridianBot.class.getClassLoader());
        this.registry = registry;
        this.jarFile = new JarFile(filePath.toFile());
        JsonObject jsonObject = (JsonObject) new JsonParser().parse(new InputStreamReader(jarFile.getInputStream(jarFile.getEntry("plugin.json"))));
        this.info = new PluginInfoImpl(jsonObject);
        this.filePath = filePath;
        this.dataFolderPath = this.filePath.getParent().resolve(this.info.getName());
        this.url = filePath.toUri().toURL();
        this.addURL(this.url);

    }


    @NotNull
    @Override
    public URL getResource(@NotNull String name) {
        return this.findResource(name);
    }

    @NotNull
    @Override
    public Enumeration<URL> getResources(@NotNull String name) throws IOException {
        return this.findResources(name);
    }

    @NotNull
    @Override
    public Class<?> loadClass(@NotNull String name) throws ClassNotFoundException {
        return super.loadClass(name, false);
    }

    @NotNull
    @Override
    protected Class<?> findClass(@NotNull String name) throws ClassNotFoundException {
        Class<?> c = this.classes.get(name);
        if (c != null) {
            return c;
        }
        c = this.registry.classes.get(name);
        if (c != null) {
            return c;
        }

        String path = name.replace(".", "/").concat(".class");
        JarEntry entry = this.jarFile.getJarEntry(path);

        if (entry != null) {
            byte[] classBytes;
            try (InputStream in = this.jarFile.getInputStream(entry)) {
                classBytes = in.readNBytes(in.available());
            } catch (IOException ex) {
                throw new ClassNotFoundException(name, ex);
            }

            CodeSigner[] signers = entry.getCodeSigners();
            CodeSource source = new CodeSource(this.url, signers);
            try {
                c = super.defineClass(name, classBytes, 0, classBytes.length, source);
            } catch (ClassFormatError ignored) {
            }
        }

        if (c == null) {
            c = super.findClass(name);
        }
        this.classes.put(name, c);
        this.registry.classes.put(name, c);
        return c;
    }

    protected boolean saveResource(@NotNull Path from, boolean replace) throws IOException {
        return this.saveResource(from, this.dataFolderPath.resolve(from), replace);
    }

    protected boolean saveResource(@NotNull Path from, @NotNull Path to, boolean replace) throws IOException {
        if (Files.exists(to)) {
            if (replace) {
                Files.deleteIfExists(to);
            } else {
                return false;
            }
        }

        JarEntry entry = this.jarFile.getJarEntry(from.toString().replace("\\", "/"));
        if (entry == null) {
            return false;
        }

        Path parentDirectoryPath = to.getParent();
        if (parentDirectoryPath != null && !Files.exists(parentDirectoryPath)) {
            Files.createDirectories(parentDirectoryPath);
        }
        try (InputStream input = this.jarFile.getInputStream(entry)) {
            Files.copy(input, to);
        }
        return true;
    }

    @NotNull
    public List<Path> getResources(@NotNull Path path, boolean deep) {
        List<Path> resources = new ArrayList<>();
        Enumeration<JarEntry> entries = this.jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            Path entryPath = Path.of(jarEntry.getName());
            if (jarEntry.isDirectory()) {
                continue;
            }
            if (!entryPath.startsWith(path)) {
                continue;
            }
            Path relativePath = path.relativize(entryPath);
            if (!deep && relativePath.getParent() != null) {
                continue;
            }
            resources.add(entryPath);
        }
        return resources;
    }

    @Override
    public void close() throws IOException {
        try {
            this.classes.forEach(this.registry.classes::remove);
            this.classes.clear();
            super.close();
        } finally {
            this.jarFile.close();
        }
    }

}
