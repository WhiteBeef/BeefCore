package ru.whitebeef.meridianbot.dto;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.whitebeef.meridianbot.entities.Permission;
import ru.whitebeef.meridianbot.entities.Role;

import java.util.HashMap;
import java.util.Map;

@Entity(name = "roles")
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    private Long id;

    @Getter
    private String name;

    @ElementCollection
    @Getter
    private Map<String, Boolean> permissions = new HashMap<>();


    public RoleDTO(Role role) {
        this.id = role.getId();
        this.name = role.getName();
        role.getPermissions().forEach((permission, state) -> this.permissions.put(permission.getPermission(), state == Permission.State.ALLOWED ? Boolean.TRUE : state == Permission.State.DENIED ? Boolean.FALSE : null));
    }
}
