package ru.whitebeef.meridianbot;

import com.google.gson.JsonObject;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import ru.whitebeef.meridianbot.command.AbstractCommand;
import ru.whitebeef.meridianbot.command.Alias;
import ru.whitebeef.meridianbot.command.CommandRegistry;
import ru.whitebeef.meridianbot.command.SimpleCommand;
import ru.whitebeef.meridianbot.command.defaultcommands.HelpCommand;
import ru.whitebeef.meridianbot.console.ConsoleConfigurator;
import ru.whitebeef.meridianbot.plugin.Plugin;
import ru.whitebeef.meridianbot.plugin.PluginRegistry;
import ru.whitebeef.meridianbot.utils.GsonUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Log4j2
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MeridianBot extends ConsoleConfigurator {

    @Getter
    private static MeridianBot instance;

    public static void main(String[] args) {
        SpringApplication.run(MeridianBot.class);
    }

    @Getter
    private File mainFolder;
    @Getter
    private JsonObject config;
    @Getter
    private CommandRegistry commandRegistry;

    @Getter
    private PluginRegistry pluginRegistry;
    @Getter
    private JDA jda;

    @Getter
    private Guild guild;

    @PostConstruct
    public void init() {
        instance = this;

        pluginRegistry = new PluginRegistry();

        try {
            log.info(Paths.get(MeridianBot.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toAbsolutePath());
            mainFolder = Paths.get(MeridianBot.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toFile().getParentFile();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        try {
            config = GsonUtils.getJsonObject(new File(mainFolder.getPath() + File.separator + "config.json")).getAsJsonObject();
        } catch (FileNotFoundException e) {
            try {
                Files.copy(Paths.get("config.json"), Paths.get("/"), StandardCopyOption.REPLACE_EXISTING);
                config = GsonUtils.getJsonObject(new File(mainFolder.getPath() + File.separator + "config.json")).getAsJsonObject();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        startJDA();
        connectToGuild();

        commandRegistry = new CommandRegistry();

        registerCommands();
        pluginRegistry.registerPlugins("plugins");

        pluginRegistry.enablePlugins();

        log.info("Запущено!");
    }


    private void registerCommands() {
        AbstractCommand.builder("help", HelpCommand.class)
                .setDescription("Команда для просмотра доступных команд")
                .setUsageMessage("help")
                .addAlias(Alias.of("рудз", false))
                .build().register();

        AbstractCommand.builder("stop", SimpleCommand.class)
                .setDescription("Остановить сервер")
                .setUsageMessage("stop")
                .setOnCommand((args) -> {
                    log.info("Остановка..");

                })
                .build().register();

        AbstractCommand.builder("plugins", SimpleCommand.class)
                .addAlias(Alias.of("pl", false))
                .setDescription("Показать список плагинов")
                .setUsageMessage("plugins")
                .setOnCommand((args) -> {
                    log.info("Список плагинов:");
                    for (Plugin plugin : pluginRegistry.getLoadedPlugins()) {
                        log.info(plugin.getInfo().getName() + ": " + (plugin.isEnabled() ? "✓" : "×"));
                    }
                })
                .build().register();
    }

    private void startJDA() {
        JDABuilder builder = JDABuilder.createDefault(config.get("token").getAsString());

        builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
        builder.setBulkDeleteSplittingEnabled(false);
        builder.setActivity(Activity.watching("на YUKIONA"));

        jda = builder.build();
    }

    private void connectToGuild() {
        guild = jda.getGuildById(config.get("guild_id").getAsString());
    }

}