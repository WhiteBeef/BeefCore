package ru.whitebeef.beefcore.handlers;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.whitebeef.beefcore.registry.UserRegistry;

@Log4j2
@Component
public class UserEventListener extends ListenerAdapter {

    private final UserRegistry userRegistry;

    @Autowired
    public UserEventListener(@NotNull JDA jda, UserRegistry userRegistry) {
        jda.addEventListener(this);
        this.userRegistry = userRegistry;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        registerOrCacheUser(event.getMessage().getAuthor());
    }

    private void registerOrCacheUser(User user) {
        userRegistry.getUserByDiscordUser(user);
    }
}
