package ru.whitebeef.meridianbot.registry;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.whitebeef.meridianbot.cache.loader.UserLoader;
import ru.whitebeef.meridianbot.entities.User;
import ru.whitebeef.meridianbot.repository.UserRepository;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@Log4j2
public class UserRegistry {

    private final LoadingCache<Long, User> users;
    private final UserRepository userRepository;

    @Autowired
    public UserRegistry(UserRepository userRepository, UserLoader userLoader, RemovalListener<Long, User> userRemovalListener) {
        this.users = CacheBuilder.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .removalListener(userRemovalListener)
                .build(userLoader);
        this.userRepository = userRepository;
    }

    public User getDefaultUser(Long discordId) {
        User user = new User();
        user.setDiscordId(discordId);
        user.setRoles(Set.of(RoleRegistry.of("default")));
        return userRepository.save(user);
    }

    public User getUserByDiscordUser(@NotNull net.dv8tion.jda.api.entities.User discordUser) {
        User user = users.getIfPresent(discordUser.getIdLong());
        if (user != null) {
            return user;
        }
        users.put(discordUser.getIdLong(), getDefaultUser(discordUser.getIdLong()));
        return users.getUnchecked(discordUser.getIdLong());
    }

}
