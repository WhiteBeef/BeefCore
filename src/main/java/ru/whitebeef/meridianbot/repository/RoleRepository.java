package ru.whitebeef.meridianbot.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.whitebeef.meridianbot.dto.RoleDTO;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleDTO, Long> {

    @NotNull RoleDTO getById(@NotNull Long id);

    Optional<RoleDTO> getByName(String name);
}
