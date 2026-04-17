package com.inventario.licoreria.modules.administrative_costs.controller;

import com.inventario.licoreria.modules.administrative_costs.dto.AdministrativeCostDTO;
import com.inventario.licoreria.modules.administrative_costs.model.AdministrativeCost;
import com.inventario.licoreria.modules.administrative_costs.service.AdministrativeCostService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.lang.NonNull;

@RestController
@RequestMapping("/api/administrative-costs")
public class AdministrativeCostController {

    private final AdministrativeCostService administrativeCostService;

    public AdministrativeCostController(AdministrativeCostService administrativeCostService) {
        this.administrativeCostService = administrativeCostService;
    }

    @GetMapping
    public List<AdministrativeCost> getAll(Authentication authentication) {
        return administrativeCostService.findAllByUsername(authentication.getName());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdministrativeCost> getById(@PathVariable @NonNull Long id) {
        return ResponseEntity.ok(administrativeCostService.findById(id));
    }

    @GetMapping("/store/{storeId}")
    public List<AdministrativeCost> getByStore(@PathVariable @NonNull Long storeId, Authentication authentication) {
        return administrativeCostService.findByStoreId(storeId, authentication.getName());
    }

    @PostMapping
    public AdministrativeCost create(@Valid @RequestBody AdministrativeCostDTO dto, Authentication authentication) {
        return administrativeCostService.create(dto, authentication.getName());
    }

    @PutMapping("/{id}")
    public AdministrativeCost update(@PathVariable @NonNull Long id, @Valid @RequestBody AdministrativeCostDTO dto, Authentication authentication) {
        return administrativeCostService.update(id, dto, authentication.getName());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Long id, Authentication authentication) {
        administrativeCostService.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
