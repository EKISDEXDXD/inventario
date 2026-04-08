package com.inventario.licoreria.modules.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class StoreCreateDTO {

    @NotBlank(message = "El nombre de la tienda es obligatorio")
    private String name;

    @NotBlank(message = "La descripción de la tienda es obligatoria")
    private String description;

    @NotNull(message = "El encargado debe ser un usuario registrado")
    private Long managerId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }
}
