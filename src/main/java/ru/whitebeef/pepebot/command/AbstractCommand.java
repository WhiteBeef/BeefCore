package ru.whitebeef.pepebot.command;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.whitebeef.pepebot.PepeBot;
import ru.whitebeef.pepebot.plugin.Plugin;
import ru.whitebeef.pepebot.utils.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class AbstractCommand {
    private static final Map<Plugin, List<AbstractCommand>> registeredCommands = new WeakHashMap<>();

    public static void unregisterAllCommands(Plugin plugin) {
        registeredCommands.getOrDefault(plugin, new ArrayList<>()).forEach(AbstractCommand::unregister);
    }

    public static Builder builder(String name, Class<? extends AbstractCommand> clazz) {
        return new Builder(name, clazz);
    }


    public static Optional<AbstractCommand> getCommand(Plugin plugin, String command) {
        String finalCommand = command.toLowerCase();
        return registeredCommands.get(plugin).stream().filter(abstractCommand -> abstractCommand.getName().equals(finalCommand)).findAny();
    }

    @Getter
    private final String name;
    @Getter
    private final String description;
    @Getter
    private final String usageMessage;
    private final Map<String, AbstractCommand> subCommands;
    @Getter
    private final Consumer<String[]> onCommand;
    @Getter
    private final Function<String[], List<String>> onTabComplete;
    @Getter
    private final List<Alias> aliases;
    @Getter
    private final int minArgsCount;

    public AbstractCommand(@NotNull String name, @NotNull String description, @NotNull String usageMessage, Consumer<String[]> onCommand,
                           Function<String[], List<String>> onTabComplete,
                           Map<String, AbstractCommand> subCommands,
                           List<Alias> aliases, int minArgsCount) {
        this.name = name.toLowerCase();
        this.description = description;
        this.usageMessage = usageMessage;
        this.onCommand = onCommand;
        this.aliases = aliases;
        this.onTabComplete = onTabComplete;
        this.subCommands = subCommands;
        this.minArgsCount = minArgsCount;
    }

    public final AbstractCommand addSubCommand(AbstractCommand abstractCommand) {
        subCommands.put(abstractCommand.getName(), abstractCommand);
        for (Alias alias : abstractCommand.getAliases()) {
            subCommands.put(alias.getName(), abstractCommand);
        }
        return this;
    }

    public final Collection<AbstractCommand> getSubCommands() {
        return subCommands.values();
    }

    @Nullable
    public final AbstractCommand getSubcommand(String str) {
        return subCommands.get(str.toLowerCase());
    }

    protected void onCommand(String[] args) {
        StandardConsumers.NO_ARGS.getConsumer().accept(args);
    }

    protected List<String> onTabComplete(String[] args) {
        return Collections.emptyList();
    }


    public final boolean execute(@NotNull String commandLabel, @NotNull String[] args) {
        Pair<Integer, AbstractCommand> pair = getCurrentCommand(args);
        AbstractCommand currentCommand = pair.right();

        args = Arrays.stream(args).skip(pair.left()).toArray(String[]::new);

        if (args.length < currentCommand.getMinArgsCount()) {
            StandardConsumers.NO_ARGS.getConsumer().accept(args);
            return true;
        }

        if (currentCommand.getOnCommand() != null) {
            currentCommand.getOnCommand().accept(args);
            return true;
        }
        currentCommand.onCommand(args);
        return true;
    }

    public final @NotNull List<String> tabComplete(@NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        if (args.length == 0) {
            return Collections.emptyList();
        }

        ArrayList<String> retList = new ArrayList<>();

        if (args.length == 1) {
            if (onTabComplete != null) {
                retList.addAll(onTabComplete.apply(args));
            } else {
                retList.addAll(this.onTabComplete(args));
            }
        } else {
            AbstractCommand subcommand = this.getSubcommand(args[0]);
            if (subcommand != null) {
                retList.addAll(subcommand.tabComplete(args[0], Arrays.copyOfRange(args, 1, args.length)));
            } else {
                if (onTabComplete != null) {
                    retList.addAll(onTabComplete.apply(args));
                } else {
                    retList.addAll(this.onTabComplete(args));
                }
            }
        }
        return retList;
    }

    private Pair<Integer, AbstractCommand> getCurrentCommand(String[] args) {
        int index = -1;
        AbstractCommand currentCommand = this;
        while (args.length > ++index) {
            String arg = args[index].toLowerCase();

            AbstractCommand lastCommand = currentCommand.getSubcommand(arg);

            if (lastCommand == null) {
                break;
            }

            currentCommand = lastCommand;
        }
        return new Pair<>(index, currentCommand);
    }

    private void loadTree(AbstractCommand parent, AbstractCommand current) {
        current.getSubCommands().forEach(command -> loadTree(current, command));
    }


    public void register() {
        loadTree(this, this);
        PepeBot.getInstance().getCommandRegistry().registerCommand(this);
    }

    public void unregister() {
        PepeBot.getInstance().getCommandRegistry().unregisterCommand(this);
    }

    public static class Builder {
        private final Class<? extends AbstractCommand> clazz;
        private final String name;
        private final Map<String, AbstractCommand> subCommands = new HashMap<>();
        private final List<Alias> aliases = new ArrayList<>();
        private Consumer<String[]> onCommand = null;
        private Function<String[], List<String>> onTabComplete = null;
        private String description = "";
        private String usageMessage = "";
        private int minArgsCount = 0;

        public Builder(String name, Class<? extends AbstractCommand> clazz) {
            this.name = name;
            this.clazz = clazz;
        }

        public Builder addSubCommand(AbstractCommand command) {
            subCommands.put(command.getName(), command);
            for (Alias alias : command.getAliases()) {
                subCommands.put(alias.getName(), command);
            }
            return this;
        }

        public Builder setOnCommand(Consumer<String[]> onCommand) {
            this.onCommand = onCommand;
            return this;
        }

        public Builder setOnTabComplete(Function<String[], List<String>> onTabComplete) {
            this.onTabComplete = onTabComplete;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setUsageMessage(String usageMessage) {
            this.usageMessage = usageMessage;
            return this;
        }

        public Builder addAlias(Alias alias) {
            this.aliases.add(alias);
            return this;
        }

        public Builder addAliases(Alias... alias) {
            this.aliases.addAll(List.of(alias));
            return this;
        }

        public Builder setMinArgsCount(int minArgsCount) {
            this.minArgsCount = minArgsCount;
            return this;
        }

        public AbstractCommand build() {
            try {
                return clazz.getDeclaredConstructor(String.class, String.class, String.class, Consumer.class, Function.class, Map.class, List.class, int.class)
                        .newInstance(name, description, usageMessage, onCommand, onTabComplete, subCommands, aliases, minArgsCount);
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
    }
}
