package ru.whitebeef.meridianbot.entities;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.whitebeef.meridianbot.dto.RoleDTO;
import ru.whitebeef.meridianbot.registry.RoleRegistry;
import ru.whitebeef.meridianbot.utils.Pair;

import java.util.HashMap;
import java.util.Map;

public class Role implements Permissible {


    @Getter
    private final Map<Permission, Permission.State> permissions = new HashMap<>();

    @Getter
    private Long id;
    @Getter
    private final String name;

    public Role(RoleRegistry roleRegistry, String name, Pair<Permission, Permission.State>... permissions) {
        this.name = name;

        for (var permission : permissions) {
            setPermission(permission.left(), permission.right());
        }
        roleRegistry.registerRole(this);
    }

    public Role(RoleDTO roleDto) {
        this.id = roleDto.getId();
        this.name = roleDto.getName();
        roleDto.getPermissions().forEach((permission, state) -> permissions.put(Permission.of(permission), Permission.State.fromBoolean(state)));
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
    public String toString() {
        return "Role{" +
                "permissions=" + permissions +
                ", id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
