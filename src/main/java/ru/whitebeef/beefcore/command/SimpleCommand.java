package ru.whitebeef.beefcore.command;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public final class SimpleCommand extends AbstractCommand {

    public static SimpleCommand newInstance(String name, String permission) {
        return new SimpleCommand(name, "", "", null, null, new HashMap<>(), Collections.emptyList(), 0);
    }

    public static SimpleCommand newInstance(String name) {
        return newInstance(name, "");
    }

    public SimpleCommand(@NotNull String name, @NotNull String description, @NotNull String usageMessage, Consumer<String[]> onCommand, Function<String[], List<String>> onTabComplete, Map<String, AbstractCommand> subCommands, List<Alias> aliases, int minArgsCount) {
        super(name, description, usageMessage, onCommand, onTabComplete, subCommands, aliases, minArgsCount);
    }
}
