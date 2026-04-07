package com.inventario.licoreria.modules.inventory.service;

import com.inventario.licoreria.modules.inventory.dto.TransactionDTO;
import com.inventario.licoreria.modules.inventory.model.Transaction;
import com.inventario.licoreria.modules.inventory.repository.TransactionRepository;
import com.inventario.licoreria.modules.products.model.Product;
import com.inventario.licoreria.modules.products.service.ProductService;
import com.inventario.licoreria.modules.users.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.lang.NonNull;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final ProductService productService; // Inyectar para actualizar stock
    private final UserService userService;

    public TransactionService(TransactionRepository transactionRepository, ProductService productService, UserService userService) {
        this.transactionRepository = transactionRepository;
        this.productService = productService;
        this.userService = userService;
    }

    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    @Transactional 
    public Transaction create(final TransactionDTO dto) {
        final Product product = productService.findById(dto.getProductId());
        userService.findById(dto.getUserId());

        final String tipo = dto.getType();
        if (!"ENTRADA".equalsIgnoreCase(tipo) && !"SALIDA".equalsIgnoreCase(tipo)) {
            throw new RuntimeException("Tipo de transacción inválido: " + tipo);
        }
        
        final int stockDelta;
        if ("ENTRADA".equalsIgnoreCase(tipo)) {
            stockDelta = dto.getQuantity();
        } else {
            stockDelta = -dto.getQuantity();
        }
        
        productService.adjustStock(product.getId(), stockDelta);
        
        final Transaction transaction = new Transaction();
        transaction.setProductId(dto.getProductId());
        transaction.setType(tipo.toUpperCase());
        transaction.setQuantity(dto.getQuantity());
        transaction.setDateTime(dto.getDateTime() != null ? dto.getDateTime() : LocalDateTime.now());
        transaction.setUserId(dto.getUserId());
        
        return transactionRepository.save(transaction);
    }

    @org.springframework.lang.NonNull
    @SuppressWarnings("null")
    public Transaction findById(@NonNull Long id) {
        return transactionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transacción no encontrada"));
    }

    // Buscar transacciones por producto
    public List<Transaction> findByProductId(@NonNull Long productId) {
        return transactionRepository.findByProductIdOrderByDateTimeDesc(productId);
    }

    // Buscar transacciones por rango de fechas
    public List<Transaction> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return transactionRepository.findByDateTimeBetweenOrderByDateTimeDesc(start, end);
    }

    @Transactional
    public Transaction update(@NonNull final Long id, final TransactionDTO dto) {
    final Transaction existing = findById(id);

    final int oldDelta = "ENTRADA".equalsIgnoreCase(existing.getType())
        ? existing.getQuantity()
        : -existing.getQuantity();

    final int newDelta = "ENTRADA".equalsIgnoreCase(dto.getType())
        ? dto.getQuantity()
        : -dto.getQuantity();

    final int delta = newDelta - oldDelta;
    productService.adjustStock(existing.getProductId(), delta);

    existing.setType(dto.getType().toUpperCase());
    existing.setQuantity(dto.getQuantity());
    existing.setDateTime(dto.getDateTime() != null ? dto.getDateTime() : existing.getDateTime());
    existing.setUserId(dto.getUserId());
    return transactionRepository.save(existing);
    }

    @Transactional
    public void delete(@NonNull final Long id) {
        final Transaction transaction = findById(id);
        int revertDelta = "ENTRADA".equalsIgnoreCase(transaction.getType()) 
            ? -transaction.getQuantity() 
            : transaction.getQuantity();
        productService.adjustStock(transaction.getProductId(), revertDelta);
        transactionRepository.delete(transaction);
    }
}