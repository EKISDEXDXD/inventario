package com.inventario.licoreria.modules.inventory.controller;

import com.inventario.licoreria.modules.inventory.dto.TransactionDTO;
import com.inventario.licoreria.modules.inventory.model.Transaction;
import com.inventario.licoreria.modules.inventory.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.lang.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public List<Transaction> getAll() {
        return transactionService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getById(@PathVariable @NonNull Long id) {
        return ResponseEntity.ok(transactionService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Transaction> create(@Valid @RequestBody TransactionDTO dto) {
        logger.info("📨 [API] POST /api/transactions - Recibida solicitud: productId={}, type={}, quantity={}, userId={}", 
            dto.getProductId(), dto.getType(), dto.getQuantity(), dto.getUserId());
        try {
            Transaction created = transactionService.create(dto);
            logger.info("✅ [API] Transacción creada exitosamente: ID = {}", created.getId());
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            logger.error("❌ [API] Error al crear transacción: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/product/{productId}")
    public List<Transaction> getByProduct(@PathVariable @NonNull Long productId) {
        return transactionService.findByProductId(productId);
    }

    @GetMapping("/range")
    public List<Transaction> getByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return transactionService.findByDateRange(start, end);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Long id) {
        transactionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}