package ru.whitebeef.meridianbot.entities;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.whitebeef.meridianbot.dto.UserDTO;
import ru.whitebeef.meridianbot.registry.RoleRegistry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserImpl implements User {
    @Getter
    private Long id;
    @Getter
    private final Map<Permission, Permission.State> permissions = new HashMap<>();
    @Getter
    private final Set<Role> roles = new HashSet<>();

    public UserImpl(UserDTO userDTO) {

        this.id = userDTO.getId();
        userDTO.getPermissions().forEach((permission, state) -> permissions.put(Permission.of(permission), state ? Permission.State.ALLOWED : Permission.State.DENIED));
        userDTO.getRoles().forEach(role -> roles.add(RoleRegistry.of(role)));

    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return hasPermission(Permission.of(permission));
    }

    @Override
    public void setPermission(@NotNull String permission, Permission.State state) {
        setPermission(Permission.of(permission), state);
    }

    @Override
    public void setPermission(@NotNull Permission permission, Permission.State state) {
        permissions.put(permission, state);
    }

    @Override
    public void setPermission(@NotNull Permission permission, @Nullable Boolean state) {
        if (state == null) {
            setPermission(permission, Permission.State.NOT_FOUND);
        } else if (state) {
            setPermission(permission, Permission.State.ALLOWED);
        } else {
            setPermission(permission, Permission.State.DENIED);
        }
    }

    @Override
    public void addRole(Role role) {
        this.roles.add(role);
    }

    @Override
    public void removeRole(Role role) {
        this.roles.remove(role);
    }
}
