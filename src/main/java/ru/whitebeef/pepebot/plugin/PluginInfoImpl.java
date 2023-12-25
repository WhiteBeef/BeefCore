package ru.whitebeef.pepebot.plugin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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

    public PluginInfoImpl(JsonObject jsonObject) {
        this.name = jsonObject.get("name").getAsString();
        this.mainClassPath = jsonObject.get("mainClass").getAsString();

        if (jsonObject.has("depends") && jsonObject.get("depends").isJsonArray()) {
            JsonArray array = jsonObject.getAsJsonArray("depends");
            this.depends = new String[array.size()];
            for (int i = 0; i < array.size(); i++) {
                this.depends[i] = array.get(i).getAsString();
            }
        } else {
            depends = new String[0];
        }

        if (jsonObject.has("softDepends") && jsonObject.get("softDepends").isJsonArray()) {
            JsonArray array = jsonObject.getAsJsonArray("softDepends");
            this.softDepends = new String[array.size()];
            for (int i = 0; i < array.size(); i++) {
                this.softDepends[i] = array.get(i).getAsString();
            }
        } else {
            softDepends = new String[0];
        }
    }

}
