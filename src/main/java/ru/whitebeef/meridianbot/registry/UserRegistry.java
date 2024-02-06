package ru.whitebeef.meridianbot.registry;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.whitebeef.meridianbot.cache.loader.UserLoader;
import ru.whitebeef.meridianbot.entities.User;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class UserRegistry {


    private final LoadingCache<Long, User> users;

    @Autowired
    public UserRegistry(UserLoader userLoader, RemovalListener<Long, User> userRemovalListener) {
        this.users = CacheBuilder.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .removalListener(userRemovalListener)
                .build(userLoader);
    }


    public User getUserById(@NotNull Long id) throws ExecutionException {
        return users.get(id);
    }

}
