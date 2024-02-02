package ru.whitebeef.meridianbot.entities;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.whitebeef.meridianbot.utils.Pair;

import java.util.HashMap;
import java.util.Map;

public class Role implements Permissible {

    private static final Map<String, Role> registeredRoles = new HashMap<>();

    public static Role of(String name, Pair<Permission, Permission.State>... permissions) {
        if (registeredRoles.containsKey(name)) {
            if (permissions.length > 0) {
                throw new IllegalArgumentException("Group with name " + name + " is already registered!");
            }
            return registeredRoles.get(name);
        }
        return new Role(name, permissions);
    }

    @Getter
    private final Map<Permission, Permission.State> permissions = new HashMap<>();

    @Getter
    private final String name;

    private Role(String name, Pair<Permission, Permission.State>... permissions) {
        this.name = name;
        registeredRoles.put(name, this);

        for (var permission : permissions) {
            setPermission(permission.left(), permission.right());
        }
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

}
