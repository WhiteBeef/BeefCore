package ru.whitebeef.meridianbot.entities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface Permissible {

    boolean hasPermission(@NotNull String permission);


    default boolean hasPermission(@NotNull Permission permission) {

        Permission.State stateInRoles = Permission.State.NOT_SET;
        if (this instanceof Roled roled) {
            for (Role role : roled.getRoles()) {
                Permission.State tempState = role.getPermissionState(permission);
                if (tempState == Permission.State.DENIED) {
                    return false;
                } else if (tempState == Permission.State.ALLOWED) {
                    stateInRoles = Permission.State.ALLOWED;
                }
            }
        }

        Permission.State stateInPermissible = getPermissionState(permission);

        return switch (stateInRoles) {
            case ALLOWED -> stateInPermissible.isAllowed() || stateInPermissible.isNotFound();
            case NOT_SET -> stateInPermissible.isAllowed();
            default -> false;
        };
    }

    default Permission.State getPermissionState(Permission permission) {
        if (getPermissions().containsKey(Permission.getStarPermission())) {
            return getPermissions().get(Permission.getStarPermission());
        }

        if (!getPermissions().containsKey(permission)) {
            if (permission.getParent() == null) {
                return Permission.State.NOT_SET;
            }
            Permission tempPermission = permission.getParent();
            while (tempPermission.getParent() != null) {
                Permission tempSuperPermission = Permission.of(tempPermission.getPermission() + ".*");

                if (getPermissions().containsKey(tempSuperPermission)) {
                    return getPermissions().get(tempSuperPermission);
                }
                tempPermission = tempPermission.getParent();
            }
            return Permission.State.NOT_SET;
        } else {
            return getPermissions().get(permission);
        }
    }

    void setPermission(@NotNull String permission, Permission.State state);

    void setPermission(@NotNull Permission permission, Permission.State state);

    void setPermission(@NotNull Permission permission, @Nullable Boolean state);

    Map<@NotNull Permission, Permission.State> getPermissions();


}
