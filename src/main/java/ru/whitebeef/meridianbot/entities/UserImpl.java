package ru.whitebeef.meridianbot.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "user")
@NoArgsConstructor
@AllArgsConstructor
public class UserImpl implements User {
    @Id
    @Getter
    private Long id;

}
