package ru.whitebeef.meridianbot.cache.loader;

import com.google.common.cache.CacheLoader;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.whitebeef.meridianbot.dto.UserDTO;
import ru.whitebeef.meridianbot.entities.User;
import ru.whitebeef.meridianbot.entities.UserImpl;
import ru.whitebeef.meridianbot.repository.UserRepository;

import java.util.HashMap;
import java.util.Set;

@Component
public class UserLoader extends CacheLoader<Long, User> {

    private final UserRepository userRepository;

    @Autowired
    public UserLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User load(@NotNull Long id) {
        return new UserImpl(userRepository.getUserById(id).orElse(new UserDTO(id, new HashMap<>(), Set.of("default"))));
    }
}
