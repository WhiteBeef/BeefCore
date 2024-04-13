package ru.whitebeef.beefcore.listeners.removal;

import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.whitebeef.beefcore.entities.User;
import ru.whitebeef.beefcore.repository.UserRepository;

@Component
public class UserRemovalListener implements RemovalListener<Long, User> {


    private final UserRepository userRepository;

    @Autowired
    public UserRemovalListener(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onRemoval(@NotNull RemovalNotification<Long, User> notification) {
        if (notification.getValue() == null) {
            return;
        }
        userRepository.save(notification.getValue());
    }
}
