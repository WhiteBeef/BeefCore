package ru.whitebeef.meridianbot.registry;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import jakarta.annotation.PreDestroy;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.whitebeef.meridianbot.MeridianBot;
import ru.whitebeef.meridianbot.dto.RoleDTO;
import ru.whitebeef.meridianbot.entities.Permission;
import ru.whitebeef.meridianbot.entities.Role;
import ru.whitebeef.meridianbot.repository.RoleRepository;
import ru.whitebeef.meridianbot.utils.Pair;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
@Component
public class RoleRegistry {
    private static RoleRegistry instance;

    public static @NotNull Role of(String name, Pair<Permission, Permission.State>... permissions) {
        if (instance.registeredRoles.containsKey(name)) {
            if (permissions.length > 0) {
                throw new IllegalArgumentException("Group with name " + name + " is already registered!");
            }
            return instance.registeredRoles.get(name);
        }
        return new Role(instance, name, permissions);
    }

    private final Map<String, Role> registeredRoles = new HashMap<>();

    private final RoleRepository roleRepository;


    @Autowired
    public RoleRegistry(RoleRepository roleRepository, MeridianBot meridianBot) {
        this.roleRepository = roleRepository;
        instance = this;

        Gson gson = new Gson();
        JsonArray rolesArray = meridianBot.getConfig().get("roles").getAsJsonArray();
        rolesArray.forEach(roleElement -> {
            RoleDTO roleDTO = gson.fromJson(roleElement, RoleDTO.class);
            Role role = new Role();
            role.setName(roleDTO.getName());
            role.setPermissionsSimple(roleDTO.getPermissions());
            registeredRoles.put(roleDTO.getName(), role);
        });
    }

    public void registerRole(Role role) {
        registeredRoles.put(role.getName(), role);
    }

    public Collection<Role> getLoadedRoles() {
        return registeredRoles.values();
    }

    @PreDestroy
    public void preDestroy() {
        roleRepository.saveAll(registeredRoles.values());
    }


}
