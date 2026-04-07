package com.inventario.licoreria.modules.products.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

public class AdjustStockDTO {
    
    @NotNull(message = "El delta de stock es obligatorio")
    private Integer delta;
    
    @NotBlank(message = "El tipo de transacción es obligatorio")
    private String transactionType; // ENTRADA o SALIDA
    
    private Long userId;
    
    public AdjustStockDTO() {
    }

    public Integer getDelta() {
        return delta;
    }

    public void setDelta(Integer delta) {
        this.delta = delta;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
