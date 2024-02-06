package ru.whitebeef.meridianbot.entities;

import lombok.Getter;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Permission {
    @RegExp
    private static final String PERMISSION_FORMAT = "(?:(?:\\*)/(?:[a-z0-9_]+(?:\\.[a-z0-9_]+)*(?:\\.\\*)?))";
    private static final Map<String, Permission> registeredPermissions = new HashMap<>();
    @Getter
    private static final Permission starPermission = Permission.of("*");

    public static Permission of(@NotNull String permission) {
        return registeredPermissions.getOrDefault(permission, new Permission(permission));
    }

    @Getter
    private final String permission;

    @Getter
    private Permission parent = null;
    private final List<Permission> child = new ArrayList<>();
    @Getter
    private boolean superPermission = false;
    @Getter
    private boolean rootPermission = false;

    private Permission(@NotNull String permission) {
        if (permission.matches(PERMISSION_FORMAT)) {
            throw new IllegalArgumentException("Invalid permission format " + permission);
        }
        this.permission = permission;
        registeredPermissions.put(permission, this);

        if (permission.endsWith("*")) {
            superPermission = true;
        }
        if (permission.contains(".")) {
            int index = permission.lastIndexOf('.');
            this.parent = Permission.of(permission.substring(0, index));
            parent.getChildren().add(this);

        } else {
            rootPermission = true;
        }
    }

    public List<Permission> getChildren() {
        return child;
    }

    @Override
    public String toString() {
        return permission;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Permission that = (Permission) o;
        return Objects.equals(permission, that.permission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(permission);
    }

    public enum State {
        ALLOWED,
        DENIED,
        NOT_FOUND;

        public boolean isAllowed() {
            return this == State.ALLOWED;
        }

        public boolean isDenied() {
            return this == State.ALLOWED;
        }

        public boolean isNotFound() {
            return this == State.ALLOWED;
        }

        public Boolean toBoolean() {
            return switch (this) {
                case ALLOWED -> Boolean.TRUE;
                case DENIED -> Boolean.FALSE;
                case NOT_FOUND -> null;
            };
        }
    }

}
