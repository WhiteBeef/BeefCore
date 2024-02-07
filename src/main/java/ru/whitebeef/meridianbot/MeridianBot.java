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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ResourceUtils;
import ru.whitebeef.meridianbot.utils.GsonUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Log4j2
@SpringBootApplication
public class MeridianBot {

    public static void main(String[] args) {
        SpringApplication.run(MeridianBot.class, args);
    }

    @Getter
    private final ApplicationContext context;
    @Getter
    private final File mainFolder;
    @Getter
    private final JsonObject config;

    @Autowired
    public MeridianBot(ApplicationContext context) {
        this.context = context;
        try {
            this.mainFolder = loadMainFolder();
            this.config = loadConfig(mainFolder);
        } catch (IOException exception) {
            log.error(exception);
            throw new RuntimeException();
        }
    }


    @Bean
    public JDA startJDA() {
        JDABuilder builder = JDABuilder.createDefault(config.get("token").getAsString())
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setEnabledIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
                .enableCache(CacheFlag.ACTIVITY)
                .setActivity(Activity.watching("на БиБиф'а"));
        return builder.build();
    }

    @Bean
    public Guild connectToGuild(JDA jda) {
        return jda.getGuildById(config.get("guild_id").getAsString());
    }

    public File loadMainFolder() throws FileNotFoundException {
        return ResourceUtils.getFile("file:./");
    }


    public JsonObject loadConfig(File mainFolder) throws IOException {
        File config = ResourceUtils.getFile("file:./config.json");
        if (!config.exists()) {
            Files.copy(ResourceUtils.getFile("jar:config.json").toPath(), mainFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
            config = ResourceUtils.getFile("file:./config.json");
        }
        return GsonUtils.getJsonObject(config).getAsJsonObject();
    }


}