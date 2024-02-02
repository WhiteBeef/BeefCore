package ru.whitebeef.meridianbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.whitebeef.meridianbot.dto.UserDTO;
import ru.whitebeef.meridianbot.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserDTO, Long> {
    Optional<User> getUserById();
}
