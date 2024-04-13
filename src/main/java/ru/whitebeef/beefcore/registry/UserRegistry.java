package ru.whitebeef.beefcore.registry;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.whitebeef.beefcore.cache.loader.UserLoader;
import ru.whitebeef.beefcore.entities.User;
import ru.whitebeef.beefcore.repository.UserRepository;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

@Component
@Log4j2
public class UserRegistry {
    @Getter
    private final LoadingCache<Long, User> users;
    private final UserRepository userRepository;
    @Getter
    private final UserLoader userLoader;

    @Autowired
    public UserRegistry(UserRepository userRepository, UserLoader userLoader, RemovalListener<Long, User> userRemovalListener) {
        this.users = CacheBuilder.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .removalListener(userRemovalListener)
                .build(userLoader);
        this.userRepository = userRepository;
        this.userLoader = userLoader;
    }

    public Collection<User> getLoadedUsers() {
        return users.asMap().values();
    }

    @PreDestroy
    public void saveAll() {
        userRepository.saveAll(users.asMap().values());
    }

}
