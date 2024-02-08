package ru.whitebeef.meridianbot.command.registrars;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import ru.whitebeef.meridianbot.command.AbstractCommand;
import ru.whitebeef.meridianbot.command.SimpleCommand;
import ru.whitebeef.meridianbot.registry.CommandRegistry;

public class StopCommandRegistrar {

    private final ApplicationContext applicationContext;
    private final CommandRegistry commandRegistry;

    @Autowired
    public StopCommandRegistrar(ApplicationContext applicationContext, CommandRegistry commandRegistry) {
        this.applicationContext = applicationContext;
        this.commandRegistry = commandRegistry;
    }

    @PostConstruct
    public void registerCommand() {
        AbstractCommand.builder("stop", SimpleCommand.class)
                .setDescription("Остановить сервер")
                .setUsageMessage("stop")
                .setOnCommand((args) -> {
                    SpringApplication.exit(applicationContext);
                })
                .build().register(commandRegistry);
    }
}
