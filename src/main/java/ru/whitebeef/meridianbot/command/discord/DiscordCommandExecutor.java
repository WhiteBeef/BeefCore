package ru.whitebeef.meridianbot.command.discord;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface DiscordCommandExecutor {
    void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event);
}
