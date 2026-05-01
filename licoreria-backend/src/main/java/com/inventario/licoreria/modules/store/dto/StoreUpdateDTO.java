package com.inventario.licoreria.modules.store.dto;

import jakarta.validation.constraints.NotBlank;

public class StoreUpdateDTO {

    @NotBlank(message = "El nombre de la tienda es obligatorio")
    private String name;

    private String accessPassword;

    public StoreUpdateDTO() {}

    public StoreUpdateDTO(String name, String accessPassword) {
        this.name = name;
        this.accessPassword = accessPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccessPassword() {
        return accessPassword;
    }

    public void setAccessPassword(String accessPassword) {
        this.accessPassword = accessPassword;
    }
}
