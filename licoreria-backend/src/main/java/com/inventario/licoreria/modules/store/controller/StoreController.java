package com.inventario.licoreria.modules.store.controller;

import com.inventario.licoreria.modules.store.dto.StoreCreateDTO;
import com.inventario.licoreria.modules.store.dto.StoreResponseDTO;
import com.inventario.licoreria.modules.store.service.StoreService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
@CrossOrigin(origins = "http://localhost:4200")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping
    public ResponseEntity<List<StoreResponseDTO>> getAll() {
        return ResponseEntity.ok(storeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(storeService.findById(id));
    }

    @PostMapping
    public ResponseEntity<StoreResponseDTO> create(@Valid @RequestBody StoreCreateDTO dto, Authentication authentication) {
        return ResponseEntity.status(201).body(storeService.create(dto, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        storeService.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
