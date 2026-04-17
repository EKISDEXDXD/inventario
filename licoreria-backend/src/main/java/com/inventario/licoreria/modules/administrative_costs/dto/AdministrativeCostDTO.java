package com.inventario.licoreria.modules.administrative_costs.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class AdministrativeCostDTO {
    
    @NotBlank(message = "El nombre del costo administrativo es obligatorio")
    private String name;
    @NotNull(message = "El costo es obligatorio")
    @Positive(message = "El costo debe ser un valor positivo")
    private BigDecimal cost;
    @NotBlank(message = "La descripción es obligatoria")
    private String description;
    @NotNull(message = "El ID de la tienda es obligatorio")
    private Long storeId;

    public AdministrativeCostDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }
}
