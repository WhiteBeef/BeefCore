package ru.whitebeef.meridianbot.command.registrars;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.whitebeef.meridianbot.command.AbstractCommand;
import ru.whitebeef.meridianbot.command.Alias;
import ru.whitebeef.meridianbot.command.defaultcommands.HelpCommand;
import ru.whitebeef.meridianbot.registry.CommandRegistry;

@Component
public class HelpCommandRegistrar {

    private final CommandRegistry commandRegistry;

    @Autowired
    public HelpCommandRegistrar(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    @PostConstruct
    public void registerCommand() {
        AbstractCommand.builder("help", HelpCommand.class)
                .setDescription("Команда для просмотра доступных команд")
                .setUsageMessage("help")
                .addAlias(Alias.of("рудз", false))
                .build().register(commandRegistry);
    }
}
