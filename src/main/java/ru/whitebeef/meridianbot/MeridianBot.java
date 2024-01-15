package ru.whitebeef.meridianbot;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import ru.whitebeef.meridianbot.command.AbstractCommand;
import ru.whitebeef.meridianbot.command.Alias;
import ru.whitebeef.meridianbot.command.CommandRegistry;
import ru.whitebeef.meridianbot.command.SimpleCommand;
import ru.whitebeef.meridianbot.command.defaultcommands.HelpCommand;
import ru.whitebeef.meridianbot.plugin.Plugin;
import ru.whitebeef.meridianbot.plugin.PluginRegistry;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;

@Log4j2
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MeridianBot {

    public static void main(String[] args) {
        SpringApplication.run(MeridianBot.class);
    }

    @Getter
    private File mainFolder;
    @Getter
    private CommandRegistry commandRegistry;

    @Getter
    private PluginRegistry pluginRegistry;
    @Getter
    private static MeridianBot instance;

    @Bean
    public void startup() {
        instance = this;

        pluginRegistry = new PluginRegistry();

        try {
            log.info(MeridianBot.class.getProtectionDomain().getCodeSource().getLocation());
            mainFolder = Paths.get(MeridianBot.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toFile().getParentFile();

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
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

}