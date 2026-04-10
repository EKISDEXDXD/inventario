package com.inventario.licoreria.modules.products.controller;

import com.inventario.licoreria.modules.products.dto.AdjustStockDTO;
import com.inventario.licoreria.modules.products.dto.ProductDTO;
import com.inventario.licoreria.modules.products.model.Product;
import com.inventario.licoreria.modules.products.service.ProductService;
import com.inventario.licoreria.modules.inventory.service.TransactionService;
import com.inventario.licoreria.modules.inventory.dto.TransactionDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.lang.NonNull;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final TransactionService transactionService;

    public ProductController(ProductService productService, TransactionService transactionService) {
        this.productService = productService;
        this.transactionService = transactionService;
    }

    @GetMapping
    public List<Product> getAll() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable @NonNull Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @GetMapping("/search")
    public List<Product> search(@RequestParam String query) {
        return productService.search(query);
    }

    @GetMapping("/search/suggestions")
    public List<Product> getSuggestions(@RequestParam String query) {
        return productService.getSuggestions(query);
    }

    @GetMapping("/store/{storeId}")
    public List<Product> getByStore(@PathVariable @NonNull Long storeId) {
        return productService.findByStoreId(storeId);
    }

    @PostMapping
    public Product create(@Valid @RequestBody ProductDTO dto) {
        return productService.create(dto);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable @NonNull Long id, @Valid @RequestBody ProductDTO dto) {
        return productService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Long id, Authentication authentication) {
        productService.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/adjust-stock")
    public Product adjustStock(
        @PathVariable @NonNull Long id, 
        @Valid @RequestBody AdjustStockDTO request
    ) {
        // Ajustar el stock
        Product updated = productService.adjustStock(id, request.getDelta());
        
        // Registrar la transacción automáticamente
        try {
            Long userId = request.getUserId() != null ? request.getUserId() : 1L; // Usar ID 1 como usuario por defecto
            
            TransactionDTO transactionDTO = new TransactionDTO();
            transactionDTO.setProductId(id);
            transactionDTO.setType(request.getDelta() > 0 ? "ENTRADA" : "SALIDA");
            transactionDTO.setQuantity(Math.abs(request.getDelta()));
            transactionDTO.setUserId(userId);
            transactionDTO.setDateTime(LocalDateTime.now());
            
            transactionService.create(transactionDTO);
        } catch (Exception e) {
            // Log the error pero no fallar la solicitud de ajuste de stock
            System.err.println("Error registrando transacción: " + e.getMessage());
        }
        
        return updated;
    }
}