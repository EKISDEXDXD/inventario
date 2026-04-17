package com.inventario.licoreria.modules.administrative_costs.model;

import com.inventario.licoreria.modules.store.model.Store;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import org.springframework.lang.NonNull;

@Entity
public class AdministrativeCost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private BigDecimal cost;
    private String description;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    public AdministrativeCost() {
    }

    @NonNull
    @SuppressWarnings("null")
    public Long getId() {
        return (Long) id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }
}
