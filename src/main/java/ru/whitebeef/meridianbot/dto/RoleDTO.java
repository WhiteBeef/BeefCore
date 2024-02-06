package ru.whitebeef.meridianbot.dto;

import lombok.Data;

import java.util.Map;

@Data
public class RoleDTO {

    private String name;
    private Map<String, Boolean> permissions;

}
