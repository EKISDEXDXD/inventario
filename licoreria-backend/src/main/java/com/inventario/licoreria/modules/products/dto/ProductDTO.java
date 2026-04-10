package com.inventario.licoreria.modules.products.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class ProductDTO {
    
    @NotBlank(message = "El nombre del producto es obligatorio")
    private String name;
    @NotBlank(message = "La descripción del producto es obligatoria")
    private String description;
    @NotNull(message = "El costo es obligatorio")
    @Positive(message = "El costo debe ser un valor positivo")
    private BigDecimal cost;
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser un valor positivo")
    private BigDecimal price;
    @NotNull(message = "El stock es obligatorio")
    @PositiveOrZero(message = "El stock debe ser mayor o igual a cero")
    private Integer stock;
    @NotNull(message = "El ID de la tienda es obligatorio")
    private Long storeId;

    public ProductDTO() {
    }

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

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }
}
