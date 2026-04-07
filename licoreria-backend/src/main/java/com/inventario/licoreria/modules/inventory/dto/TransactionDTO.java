package com.inventario.licoreria.modules.inventory.dto;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.springframework.lang.NonNull;

public class TransactionDTO {

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productId;
    @NotNull(message = "El tipo de transacción es obligatorio")
    @Pattern(regexp = "^(ENTRADA|SALIDA)$", message = "El tipo debe ser ENTRADA o SALIDA")
    private String type;
    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a 0")
    private Integer quantity;
    private LocalDateTime dateTime;
    @NotNull(message = "El ID del usuario es obligatorio")
    private Long userId;

    public TransactionDTO() {
    }

    @NonNull
    @SuppressWarnings("null")
    public Long getProductId() {
        return (Long) productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    @NonNull
    @SuppressWarnings("null")
    public String getType() {
        return (String) type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @NonNull
    @SuppressWarnings("null")
    public Integer getQuantity() {
        return (Integer) quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @NonNull
    @SuppressWarnings("null")
    public Long getUserId() {
        return (Long) userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}