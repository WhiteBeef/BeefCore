package ru.whitebeef.meridianbot;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ResourceUtils;
import ru.whitebeef.meridianbot.utils.GsonUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Getter
@Log4j2
@SpringBootApplication
public class MeridianBot {


    public static void main(String[] args) {
        SpringApplication.run(MeridianBot.class, args);
    }

    private final File mainFolder;
    private final JsonObject config;


    public MeridianBot() {
        try {
            this.mainFolder = loadMainFolder();
            this.config = loadConfig();
        } catch (IOException | URISyntaxException exception) {
            log.error(exception);
            throw new RuntimeException();
        }
    }


    @Bean
    public JDA startJDA() throws InterruptedException {
        JDABuilder builder = JDABuilder.createDefault(config.get("token").getAsString())
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setEnabledIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
                .enableCache(CacheFlag.ACTIVITY)
                .setActivity(Activity.watching("на БиБиф'а"));
        return builder.build().awaitReady();
    }

    @Bean
    public Guild connectToGuild(JDA jda) {
        return jda.getGuildById(config.get("guild_id").getAsString());
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