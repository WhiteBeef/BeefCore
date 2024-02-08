package ru.whitebeef.meridianbot.registry;

import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import ru.whitebeef.meridianbot.command.AbstractCommand;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Component
public class CommandRegistry {

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


}
