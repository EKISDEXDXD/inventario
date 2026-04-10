package com.inventario.licoreria.modules.inventory.dto;

import java.time.LocalDateTime;

public class TransactionResponseDTO {
    private Long id;
    private Long productId;
    private String type;
    private Integer quantity;
    private LocalDateTime dateTime;
    private String username;

    public TransactionResponseDTO() {
    }

    public TransactionResponseDTO(Long id, Long productId, String type, Integer quantity, LocalDateTime dateTime, String username) {
        this.id = id;
        this.productId = productId;
        this.type = type;
        this.quantity = quantity;
        this.dateTime = dateTime;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getQuantity() {
        return quantity;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
