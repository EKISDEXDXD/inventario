package com.inventario.licoreria.modules.administrative_costs.controller;

import com.inventario.licoreria.modules.administrative_costs.dto.AdministrativeCostMovementDTO;
import com.inventario.licoreria.modules.administrative_costs.model.AdministrativeCostMovement;
import com.inventario.licoreria.modules.administrative_costs.service.AdministrativeCostMovementService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.lang.NonNull;

@RestController
@RequestMapping("/api/administrative-cost-movements")
public class AdministrativeCostMovementController {

    private final AdministrativeCostMovementService movementService;

    public AdministrativeCostMovementController(AdministrativeCostMovementService movementService) {
        this.movementService = movementService;
    }

    @GetMapping
    public List<AdministrativeCostMovement> getAll(Authentication authentication) {
        return movementService.findAllByUsername(authentication.getName());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdministrativeCostMovement> getById(@PathVariable @NonNull Long id) {
        return ResponseEntity.ok(movementService.findById(id));
    }

    @GetMapping("/store/{storeId}")
    public List<AdministrativeCostMovement> getByStore(@PathVariable @NonNull Long storeId, Authentication authentication) {
        return movementService.findByStoreId(storeId, authentication.getName());
    }

    @GetMapping("/cost/{costId}")
    public List<AdministrativeCostMovement> getByCost(@PathVariable @NonNull Long costId, Authentication authentication) {
        return movementService.findByCostId(costId, authentication.getName());
    }

    @PostMapping
    public AdministrativeCostMovement create(@Valid @RequestBody AdministrativeCostMovementDTO dto, Authentication authentication) {
        return movementService.create(dto, authentication.getName());
    }

    @PutMapping("/{id}")
    public AdministrativeCostMovement update(@PathVariable @NonNull Long id, @Valid @RequestBody AdministrativeCostMovementDTO dto, Authentication authentication) {
        return movementService.update(id, dto, authentication.getName());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Long id, Authentication authentication) {
        movementService.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
