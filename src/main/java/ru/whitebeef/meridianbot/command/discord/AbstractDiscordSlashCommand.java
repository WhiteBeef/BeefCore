package ru.whitebeef.meridianbot.command.discord;

import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.whitebeef.meridianbot.entities.Permission;
import ru.whitebeef.meridianbot.registry.DiscordSlashCommandRegistry;

import java.util.Objects;
import java.util.function.Consumer;

public class AbstractDiscordSlashCommand implements DiscordCommandExecutor {

    private static final MessageCreateData NO_BEHAVIOR = MessageCreateData.fromContent("У команды нет поведения!");

    public static AbstractDiscordSlashCommand.Builder builder(CommandData commandData, Class<? extends AbstractDiscordSlashCommand> clazz) {
        return new AbstractDiscordSlashCommand.Builder(commandData, clazz);
    }

    @Getter
    private final Permission permission;

    @Getter
    private final CommandData commandData;

    private final Consumer<SlashCommandInteractionEvent> onCommand;

    public AbstractDiscordSlashCommand(@NotNull CommandData commandData, @Nullable Permission permission, @Nullable Consumer<SlashCommandInteractionEvent> onCommand) {
        this.commandData = commandData;
        this.permission = Objects.requireNonNullElseGet(permission, () -> Permission.of("*"));
        this.onCommand = onCommand;
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

    public static class Builder {
        private final Class<? extends AbstractDiscordSlashCommand> clazz;
        private final CommandData commandData;
        private String permission = "*";
        private Consumer<SlashCommandInteractionEvent> onCommand;

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


        public AbstractDiscordSlashCommand build() {
            try {

                return clazz.getDeclaredConstructor(CommandData.class, Permission.class, Consumer.class)
                        .newInstance(commandData, Permission.of(permission), onCommand);
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
    }

}
