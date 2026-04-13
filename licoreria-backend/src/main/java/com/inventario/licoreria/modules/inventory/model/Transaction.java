package com.inventario.licoreria.modules.inventory.model;

import com.inventario.licoreria.modules.products.model.Product;
import com.inventario.licoreria.modules.users.model.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.springframework.lang.NonNull;

@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Transaction() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NonNull
    @SuppressWarnings("null")
    public Product getProduct() {
        return (Product) product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    // Helper method for backwards compatibility
    @NonNull
    @SuppressWarnings("null")
    public Long getProductId() {
        return product != null ? product.getId() : null;
    }

    public void setProductId(Long productId) {
        // This is handled by the product relationship now
        // But kept for backwards compatibility
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

    @NonNull
    @SuppressWarnings("null")
    public User getUser() {
        return (User) user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Helper method for backwards compatibility
    @NonNull
    @SuppressWarnings("null")
    public Long getUserId() {
        return user != null ? user.getId() : null;
    }

    public void setUserId(Long userId) {
        // This is handled by the user relationship now
        // But kept for backwards compatibility
    }
}
