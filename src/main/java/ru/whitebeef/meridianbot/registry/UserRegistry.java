package ru.whitebeef.meridianbot.registry;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.whitebeef.meridianbot.cache.loader.UserLoader;
import ru.whitebeef.meridianbot.entities.User;
import ru.whitebeef.meridianbot.repository.UserRepository;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
@Log4j2
public class UserRegistry {

    private final LoadingCache<Long, User> users;
    private final UserRepository userRepository;
    private final UserLoader userLoader;
    private final JDA jda;

    @Autowired
    public UserRegistry(JDA jda, UserRepository userRepository, UserLoader userLoader, RemovalListener<Long, User> userRemovalListener) {
        this.jda = jda;
        this.users = CacheBuilder.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .removalListener(userRemovalListener)
                .build(userLoader);
        this.userRepository = userRepository;
        this.userLoader = userLoader;
    }


    public User getUserByDiscordUser(@NotNull net.dv8tion.jda.api.entities.User discordUser) {
        User user = null;
        try {
            user = users.get(discordUser.getIdLong());
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        if (user != null) {
            return user;
        }

        users.put(discordUser.getIdLong(), userLoader.getDefaultUser(discordUser.getIdLong()));
        return users.getUnchecked(discordUser.getIdLong());
    }

    public net.dv8tion.jda.api.entities.User getDiscordUserByUser(@NotNull User discordUser) {
        return jda.getUserById(discordUser.getDiscordId());
    }

    public Collection<User> getLoadedUsers() {
        return users.asMap().values();
    }

}
