package ru.whitebeef.beefcore;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.ResourceUtils;
import ru.whitebeef.beefcore.utils.GsonUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

@Getter
@Log4j2
@SpringBootApplication
public class BeefCore {


    private final File mainFolder;
    private final JsonObject config;
    public BeefCore() {
        try {
            this.mainFolder = loadMainFolder();
            this.config = loadConfig();
        } catch (IOException | URISyntaxException exception) {
            log.error(exception);
            throw new RuntimeException();
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(BeefCore.class, args);
    }

    public File loadMainFolder() throws FileNotFoundException {
        return ResourceUtils.getFile("file:./");
    }


    public JsonObject loadConfig() throws IOException, URISyntaxException {
        File config = ResourceUtils.getFile("file:./config.json");
        if (!config.exists()) {
            Files.copy(getClass().getClassLoader().getResourceAsStream("./config.json"), config.toPath());
            config = ResourceUtils.getFile("file:./config.json");
        }
        return GsonUtils.getJsonObject(config).getAsJsonObject();
    }


}