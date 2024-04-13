package ru.whitebeef.beefcore.command.defaultcommands;

import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import ru.whitebeef.beefcore.command.AbstractCommand;
import ru.whitebeef.beefcore.command.Alias;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@Log4j2
public class HelpCommand extends AbstractCommand {

    public HelpCommand(@NotNull String name, @NotNull String description, @NotNull String usageMessage, Consumer<String[]> onCommand, Function<String[], List<String>> onTabComplete, Map<String, AbstractCommand> subCommands, List<Alias> aliases, int minArgsCount) {
        super(name, description, usageMessage, onCommand, onTabComplete, subCommands, aliases, minArgsCount);
    }

    @Override
    public void onCommand(String[] args) {
        log.info("Help:");
        for (AbstractCommand command : getCommandRegistry().getRegisteredCommands()) {
            log.info(command.getName() + ": " + command.getDescription() + " - " + command.getUsageMessage());
        }
    }
}
