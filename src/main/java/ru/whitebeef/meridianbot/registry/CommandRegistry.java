package ru.whitebeef.meridianbot.registry;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ru.whitebeef.meridianbot.MeridianBot;
import ru.whitebeef.meridianbot.command.AbstractCommand;
import ru.whitebeef.meridianbot.command.Alias;
import ru.whitebeef.meridianbot.command.SimpleCommand;
import ru.whitebeef.meridianbot.command.defaultcommands.HelpCommand;
import ru.whitebeef.meridianbot.plugin.Plugin;
import ru.whitebeef.meridianbot.plugin.PluginRegistry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@Log4j2
@Component
public class CommandRegistry {

    private final MeridianBot meridianBot;
    private final PluginRegistry pluginRegistry;

    @Autowired
    public CommandRegistry(MeridianBot meridianBot, PluginRegistry pluginRegistry) {
        this.meridianBot = meridianBot;
        this.pluginRegistry = pluginRegistry;
    }

    private final Map<String, AbstractCommand> commandMap = new HashMap<>();
    private final Map<String, AbstractCommand> aliasesCommandMap = new HashMap<>();

    public Collection<AbstractCommand> getRegisteredCommands() {
        return commandMap.values();
    }

    public Collection<AbstractCommand> getRegisteredAliasesCommands() {
        return aliasesCommandMap.values();
    }

    public void registerCommand(AbstractCommand command) {
        command.getAliases().forEach(commandAlias -> aliasesCommandMap.put(commandAlias.getName(), command));
        commandMap.put(command.getName(), command);
    }

    public void unregisterCommand(@NotNull AbstractCommand command) {
        command.getAliases().forEach(commandAlias -> aliasesCommandMap.remove(commandAlias.getName()));
        commandMap.remove(command.getName());
    }

    @Nullable
    public AbstractCommand getCommand(String command) {
        if (commandMap.containsKey(command)) {
            return commandMap.get(command);
        } else {
            return aliasesCommandMap.get(command);
        }
    }

    @Bean
    private void registerCommands() {
        AbstractCommand.builder("help", HelpCommand.class)
                .setDescription("Команда для просмотра доступных команд")
                .setUsageMessage("help")
                .addAlias(Alias.of("рудз", false))
                .build().register(this);

        AbstractCommand.builder("stop", SimpleCommand.class)
                .setDescription("Остановить сервер")
                .setUsageMessage("stop")
                .setOnCommand((args) -> {
                    SpringApplication.exit(meridianBot.getContext());
                })
                .build().register(this);

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
                .build().register(this);
    }

}
