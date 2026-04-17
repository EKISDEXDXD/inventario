package com.inventario.licoreria.modules.administrative_costs.model;

import com.inventario.licoreria.modules.users.model.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.lang.NonNull;

@Entity
@Table(name = "administrative_cost_movement")
public class AdministrativeCostMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "administrative_cost_id", nullable = false)
    private AdministrativeCost administrativeCost;

    @Column(nullable = false)
    private String type; // PAGO, AJUSTE, DEVOLUCIÓN

    @Column(nullable = false)
    private BigDecimal amountPaid;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public AdministrativeCostMovement() {
    }

    @NonNull
    @SuppressWarnings("null")
    public Long getId() {
        return (Long) id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NonNull
    @SuppressWarnings("null")
    public AdministrativeCost getAdministrativeCost() {
        return (AdministrativeCost) administrativeCost;
    }

    public void setAdministrativeCost(AdministrativeCost administrativeCost) {
        this.administrativeCost = administrativeCost;
    }

    @NonNull
    @SuppressWarnings("null")
    public Long getAdministrativeCostId() {
        return administrativeCost != null ? administrativeCost.getId() : null;
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

    @NonNull
    @SuppressWarnings("null")
    public User getUser() {
        return (User) user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
