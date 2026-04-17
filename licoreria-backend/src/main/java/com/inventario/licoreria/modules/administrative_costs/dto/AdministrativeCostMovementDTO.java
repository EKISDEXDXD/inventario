package com.inventario.licoreria.modules.administrative_costs.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;

public class AdministrativeCostMovementDTO {
    
    @NotNull(message = "El ID del costo administrativo es obligatorio")
    private Long administrativeCostId;
    @NotBlank(message = "El tipo de movimiento es obligatorio")
    private String type; // PAGO, AJUSTE, DEVOLUCIÓN
    @NotNull(message = "El monto pagado es obligatorio")
    @Positive(message = "El monto pagado debe ser un valor positivo")
    private BigDecimal amountPaid;
    @NotNull(message = "La fecha es obligatoria")
    private LocalDateTime dateTime;
    private Long userId; // No requerido - se obtiene del token

    public AdministrativeCostMovementDTO() {
    }

    public Long getAdministrativeCostId() {
        return administrativeCostId;
    }

    public void setAdministrativeCostId(Long administrativeCostId) {
        this.administrativeCostId = administrativeCostId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
