package com.inventario.licoreria.modules.users.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateUserDTO {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String username;

    public UpdateUserDTO() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
