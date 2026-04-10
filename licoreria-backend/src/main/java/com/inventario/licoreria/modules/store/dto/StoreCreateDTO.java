package com.inventario.licoreria.modules.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class StoreCreateDTO {

    @NotBlank(message = "El nombre de la tienda es obligatorio")
    private String name;

    @NotBlank(message = "La descripción de la tienda es obligatoria")
    private String description;

    @NotBlank(message = "La dirección de la tienda es obligatoria")
    private String address;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
