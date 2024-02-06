package ru.whitebeef.meridianbot.dto;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.whitebeef.meridianbot.entities.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    @Id
    @Getter
    private Long id;

    @Getter
    @ElementCollection
    private Map<String, Boolean> permissions = new HashMap<>();

    @Getter
    @ElementCollection
    private Set<String> roles = new HashSet<>();

    public UserDTO(User user) {
        this.id = user.getId();
        user.getPermissions().forEach((permission, state) -> this.permissions.put(permission.getPermission(), state.toBoolean()));
    }
}
