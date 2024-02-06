package ru.whitebeef.meridianbot.entities;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.whitebeef.meridianbot.registry.RoleRegistry;
import ru.whitebeef.meridianbot.utils.Pair;

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
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(unique = true, nullable = false)
    private Long id;
    @Getter
    @Setter
    @Column(unique = true, nullable = false)
    private String name;

    @Getter
    @ElementCollection
    @Column(name = "permissions")
    private Map<String, Boolean> permissionsSimple = new HashMap<>();

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
        permissions.put(permission, state);
    }

    public void setPermissionsSimple(Map<String, Boolean> permissionsSimple) {
        this.permissionsSimple = permissionsSimple;
        permissionsSimple.forEach((permission, value) -> this.permissions.put(Permission.of(permission), Permission.State.fromBoolean(value)));
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
