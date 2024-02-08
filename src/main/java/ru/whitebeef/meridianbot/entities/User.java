package ru.whitebeef.meridianbot.entities;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor
@Entity
@Table(name = "Users")
public class User implements Roled, Permissible {

    @Id
    @Getter
    @Setter
    @Column(unique = true, nullable = false)
    private Long discordId;
    @Getter
    @Transient
    private final Map<Permission, Permission.State> permissions = new HashMap<>();

    @Getter
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "permissions")
    private Map<String, Boolean> permissionsSimple = new HashMap<>();

    @Getter
    @Setter
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "name")
    private Set<Role> roles = new HashSet<>();

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
        if (state == Permission.State.NOT_SET) {
            permissions.remove(permission);
            permissionsSimple.remove(permission.getPermission());
            return;
        }
        permissions.put(permission, state);
        permissionsSimple.put(permission.getPermission(), state.toBoolean());

    }

    @Override
    public void setPermission(@NotNull Permission permission, @Nullable Boolean state) {
        if (state == null) {
            setPermission(permission, Permission.State.NOT_SET);
        } else if (state) {
            setPermission(permission, Permission.State.ALLOWED);
        } else {
            setPermission(permission, Permission.State.DENIED);
        }
    }

    public void setPermissionsSimple(Map<String, Boolean> permissionsSimple) {
        this.permissionsSimple = permissionsSimple;
        permissionsSimple.forEach((permission, value) -> this.permissions.put(Permission.of(permission), Permission.State.fromBoolean(value)));
    }

    @Override
    public void addRole(Role role) {
        this.roles.add(role);
    }

    @Override
    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    @PostLoad
    public void postLoad() {
        permissionsSimple.forEach((permission, value) -> this.permissions.put(Permission.of(permission), Permission.State.fromBoolean(value)));
    }

}
