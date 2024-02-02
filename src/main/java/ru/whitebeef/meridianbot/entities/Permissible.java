package ru.whitebeef.meridianbot.entities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface Permissible {

    boolean hasPermission(@NotNull String permission);


    default boolean hasPermission(@NotNull Permission permission) {
        if (getPermissions().containsKey(Permission.getStarPermission())) {
            return getPermissions().get(Permission.getStarPermission()) == Permission.State.ALLOWED;
        }

        if (!getPermissions().containsKey(permission)) {
            if (permission.getParent() == null) {
                return false;
            }
            Permission tempPermission = permission.getParent();
            while (tempPermission.getParent() != null) {
                Permission tempSuperPermission = Permission.of(tempPermission.getPermission() + ".*");

                if (getPermissions().containsKey(tempSuperPermission)) {
                    return getPermissions().get(tempSuperPermission) == Permission.State.ALLOWED;
                }
                tempPermission = tempPermission.getParent();
            }
            return false;
        } else {
            return getPermissions().get(permission) == Permission.State.ALLOWED;
        }
    }

    void setPermission(@NotNull String permission, Permission.State state);

    void setPermission(@NotNull Permission permission, Permission.State state);

    void setPermission(@NotNull Permission permission, @Nullable Boolean state);

    Map<@NotNull Permission, Permission.State> getPermissions();
}
