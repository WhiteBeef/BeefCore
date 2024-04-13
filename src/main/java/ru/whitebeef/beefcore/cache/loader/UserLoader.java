package ru.whitebeef.beefcore.cache.loader;

import com.google.common.cache.CacheLoader;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.whitebeef.beefcore.entities.User;
import ru.whitebeef.beefcore.registry.RoleRegistry;
import ru.whitebeef.beefcore.repository.UserRepository;

import java.util.Set;

@Component
public class UserLoader extends CacheLoader<Long, User> {

    private final UserRepository userRepository;

    @Autowired
    public UserLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User load(@NotNull Long discordId) {
        return userRepository.getUserByDiscordId(discordId).orElseGet(() -> getDefaultUser(discordId));
    }

    public User getDefaultUser(Long discordId) {
        User user = new User();
        user.setDiscordId(discordId);
        user.setRoles(Set.of(RoleRegistry.of("default")));
        return userRepository.save(user);
    }
}
