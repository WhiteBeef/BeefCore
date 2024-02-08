package ru.whitebeef.meridianbot.command.registrars;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import ru.whitebeef.meridianbot.command.AbstractCommand;
import ru.whitebeef.meridianbot.command.SimpleCommand;
import ru.whitebeef.meridianbot.command.defaultcommands.permission.PermissionGetUserCommand;
import ru.whitebeef.meridianbot.command.defaultcommands.permission.PermissionSetUserCommand;
import ru.whitebeef.meridianbot.registry.CommandRegistry;

@Component
public class PermissionCommandRegistrar {

    private final CommandRegistry commandRegistry;

    public PermissionCommandRegistrar(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    @PostConstruct
    public void registerCommand() {
        AbstractCommand.builder("permission", SimpleCommand.class)
                .addSubCommand(AbstractCommand.builder("get", PermissionGetUserCommand.class).build())
                .addSubCommand(AbstractCommand.builder("set", PermissionSetUserCommand.class).build())
                .setDescription("Управление правами")
                .setUsageMessage("permission <get|set>")
                .build().register(commandRegistry);
    }
}
