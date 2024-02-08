package ru.whitebeef.meridianbot.command.discord;

import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.whitebeef.meridianbot.entities.Permission;
import ru.whitebeef.meridianbot.registry.DiscordSlashCommandRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class AbstractDiscordSlashCommand implements DiscordCommandExecutor, DiscordCommandTabCompleter {

    private static final MessageCreateData NO_BEHAVIOR = MessageCreateData.fromContent("У команды нет поведения!");

    public static AbstractDiscordSlashCommand.Builder builder(CommandData commandData, Class<? extends AbstractDiscordSlashCommand> clazz) {
        return new AbstractDiscordSlashCommand.Builder(commandData, clazz);
    }

    @Getter
    private final Permission permission;

    @Getter
    private final CommandData commandData;

    private final Consumer<SlashCommandInteractionEvent> onCommand;

    private final Map<String, Function<AutoCompleteQuery, List<Command.Choice>>> onTabComplete;

    public AbstractDiscordSlashCommand(@NotNull CommandData commandData, @Nullable Permission permission, @Nullable Consumer<SlashCommandInteractionEvent> onCommand, Map<String, Function<AutoCompleteQuery, List<Command.Choice>>> onTabComplete) {
        this.commandData = commandData;
        this.permission = Objects.requireNonNullElseGet(permission, () -> Permission.of("*"));
        this.onCommand = onCommand;
        this.onTabComplete = onTabComplete;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (onCommand != null) {
            onCommand.accept(event);
            return;
        }
        event.reply(NO_BEHAVIOR).queue();
    }

    public void register(DiscordSlashCommandRegistry registry) {
        registry.registerCommand(this);
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        event.replyChoices(onTabComplete.get(event.getFocusedOption().getName()).apply(event.getFocusedOption())).queue();
    }

    public static class Builder {
        private final Class<? extends AbstractDiscordSlashCommand> clazz;
        private final CommandData commandData;
        private String permission = "*";
        private Consumer<SlashCommandInteractionEvent> onCommand;
        private final Map<String, Function<AutoCompleteQuery, List<Command.Choice>>> onTabComplete = new HashMap<>();

        public Builder(CommandData commandData, Class<? extends AbstractDiscordSlashCommand> clazz) {
            this.commandData = commandData;
            this.clazz = clazz;
        }

        public AbstractDiscordSlashCommand.Builder setOnCommand(Consumer<SlashCommandInteractionEvent> onCommand) {
            this.onCommand = onCommand;
            return this;
        }


        public AbstractDiscordSlashCommand.Builder setPermission(String permission) {
            this.permission = permission;
            return this;
        }

        public AbstractDiscordSlashCommand.Builder addAutoComplete(String name, Function<AutoCompleteQuery, List<Command.Choice>> onTabComplete) {
            this.onTabComplete.put(name, onTabComplete);
            return this;
        }


        public AbstractDiscordSlashCommand build() {
            try {

                return clazz.getDeclaredConstructor(CommandData.class, Permission.class, Consumer.class, Map.class)
                        .newInstance(commandData, Permission.of(permission), onCommand, onTabComplete);
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
    }

}
