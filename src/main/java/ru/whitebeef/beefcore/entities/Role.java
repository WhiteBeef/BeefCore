package ru.whitebeef.beefcore.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.whitebeef.beefcore.registry.RoleRegistry;
import ru.whitebeef.beefcore.utils.Pair;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "Roles")
@NoArgsConstructor
public class Role implements Permissible {


    @Getter
    @Transient
    private final Map<Permission, Permission.State> permissions = new HashMap<>();
    @Getter
    @ElementCollection
    @Column(name = "permissions")
    private Map<String, Boolean> permissionsSimple = new HashMap<>();
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(unique = true, nullable = false)
    private Long id;
    @Getter
    @Setter
    @Column(unique = true, nullable = false)
    private String name;


    public Role(RoleRegistry roleRegistry, String name, Pair<Permission, Permission.State>... permissions) {
        this.name = name;

        for (var permission : permissions) {
            setPermission(permission.left(), permission.right());
        }
        roleRegistry.registerRole(this);
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
        if (state == Permission.State.NOT_SET) {
            permissions.remove(permission);
            permissionsSimple.remove(permission.getPermission());
            return;
        }
        permissions.put(permission, state);
        permissionsSimple.put(permission.getPermission(), state.toBoolean());
    }

    public void setPermissionsSimple(Map<String, Boolean> permissionsSimple) {
        this.permissionsSimple = permissionsSimple;
        permissionsSimple.forEach((permission, value) -> this.permissions.put(Permission.of(permission), Permission.State.fromBoolean(value)));
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

    @PostLoad
    public void onPostLoad() {
        permissionsSimple.forEach((permission, value) -> this.permissions.put(Permission.of(permission), Permission.State.fromBoolean(value)));
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
