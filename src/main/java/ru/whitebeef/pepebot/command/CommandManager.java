package ru.whitebeef.pepebot.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.whitebeef.pepebot.plugin.Plugin;
import ru.whitebeef.pepebot.plugin.PluginInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandManager {
    private final Map<String, AbstractCommand> commandMap = new HashMap<>();

    public Collection<AbstractCommand> getRegisteredCommands() {
        return commandMap.values();
    }

    public void registerCommand(Plugin plugin, AbstractCommand command) {
        getAvailableCommands(plugin.getInfo(), command).forEach(commandAlias -> {
            if (commandMap.containsKey(commandAlias)) {
                return;
            }
            commandMap.put(commandAlias, command);
        });

    }

    public void registerCommandWithoutPlugin(AbstractCommand command) {
        if (commandMap.containsKey(command.getName())) {
            return;
        }
        commandMap.put(command.getName(), command);

        command.getAliases().forEach(alias -> {
            if (commandMap.containsKey(alias.getName())) {
                return;
            }
            commandMap.put(alias.getName(), command);
        });

    }

    public void unregisterCommand(Plugin plugin, @NotNull AbstractCommand command) {
        getAvailableCommands(plugin.getInfo(), command).forEach(commandAlias -> {
            commandMap.remove(commandAlias, command);
        });
    }

    @Nullable
    public AbstractCommand getCommand(String command) {
        return commandMap.get(command);
    }

    private List<String> getAvailableCommands(PluginInfo info, AbstractCommand command) {
        List<String> list = new ArrayList<>();

        list.add(info.getName() + ":" + command.getName());
        list.add(command.getName());
        for (Alias alias : command.getAliases()) {
            list.add(info.getName() + ":" + alias.getName());
            list.add(alias.getName());
        }
        return list;
    }


}
