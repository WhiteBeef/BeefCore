package ru.whitebeef.meridianbot.registry;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.whitebeef.meridianbot.command.discord.AbstractDiscordSlashCommand;
import ru.whitebeef.meridianbot.entities.User;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@Component
public class DiscordSlashCommandRegistry extends ListenerAdapter {
    private static final MessageCreateData COMMAND_NOT_FOUND = MessageCreateData.fromContent("Команда не найдена!");
    private static final MessageCreateData NO_PERMISSIONS = MessageCreateData.fromContent("Недостаточно прав!");


    private final Guild guild;
    private final UserRegistry userRegistry;

    @Autowired
    public DiscordSlashCommandRegistry(JDA jda, Guild guild, UserRegistry userRegistry) {
        jda.addEventListener(this);
        this.guild = guild;
        this.userRegistry = userRegistry;
    }

    // label, command
    private final Map<String, AbstractDiscordSlashCommand> commands = new HashMap<>();

    public void registerCommand(AbstractDiscordSlashCommand abstractDiscordSlashCommand) {
        guild.upsertCommand(abstractDiscordSlashCommand.getCommandData()).queue();
        commands.put(abstractDiscordSlashCommand.getCommandData().getName(), abstractDiscordSlashCommand);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);

        log.info("Команда пришла: " + event.getName());

        AbstractDiscordSlashCommand command = commands.get(event.getName());

        if (command == null) {
            event.reply(COMMAND_NOT_FOUND).queue();
            return;
        }

        User user = userRegistry.getUserByDiscordUser(event.getUser());

        if (!user.hasPermission(command.getPermission())) {
            event.reply(NO_PERMISSIONS).queue();
            return;
        }
        command.onSlashCommandInteraction(event);
    }

    @PostConstruct
    public void registerCommands() {

        AbstractDiscordSlashCommand.builder(new CommandDataImpl("test", "test command"), AbstractDiscordSlashCommand.class).build().register(this);
    }
}
