package ru.whitebeef.meridianbot.dto;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    @Id
    @Getter
    private Long id;

    @Getter
    @ElementCollection
    private final Map<String, Boolean> permissions = new HashMap<>();

    @Getter
    @ElementCollection
    private final Set<String> roles = new HashSet<>();
}
