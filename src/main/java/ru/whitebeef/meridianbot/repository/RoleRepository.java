package ru.whitebeef.meridianbot.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.whitebeef.meridianbot.entities.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @NotNull Role getById(@NotNull Long id);
}
