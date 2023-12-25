package ru.whitebeef.pepebot.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class GsonUtils {


    @Getter
    private static final Gson gson = new Gson();

    @NotNull
    public static JsonElement getJsonObject(File file) throws FileNotFoundException {
        if (!file.exists()) {
            return new JsonObject();
        }
        return new JsonParser().parse(new FileReader(file));
    }

}
