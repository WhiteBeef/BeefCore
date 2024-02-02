package ru.whitebeef.meridianbot.entities;

import java.util.Set;

public interface Roled {

    Set<Role> getRoles();

    void addRole(Role role);

    void removeRole(Role role);
}
