package ru.whitebeef.meridianbot.cache.loader;

import com.google.common.cache.CacheLoader;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.whitebeef.meridianbot.entities.User;
import ru.whitebeef.meridianbot.repository.UserRepository;

@Component
public class UserLoader extends CacheLoader<Long, User> {

    private final UserRepository userRepository;

    @Autowired
    public UserLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User load(@NotNull Long discordId) {
        return userRepository.getUserByDiscordId(discordId).orElseThrow(() -> new RuntimeException("ОИШБКААА"));
    }
}
