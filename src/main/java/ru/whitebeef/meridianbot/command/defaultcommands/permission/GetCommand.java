package ru.whitebeef.meridianbot.command.defaultcommands.permission;

import org.jetbrains.annotations.NotNull;
import ru.whitebeef.meridianbot.command.AbstractCommand;
import ru.whitebeef.meridianbot.command.Alias;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class GetCommand extends AbstractCommand {
    public GetCommand(@NotNull String name, @NotNull String description, @NotNull String usageMessage, Consumer<String[]> onCommand, Function<String[], List<String>> onTabComplete, Map<String, AbstractCommand> subCommands, List<Alias> aliases, int minArgsCount) {
        super(name, description, usageMessage, onCommand, onTabComplete, subCommands, aliases, minArgsCount);
    }


}
