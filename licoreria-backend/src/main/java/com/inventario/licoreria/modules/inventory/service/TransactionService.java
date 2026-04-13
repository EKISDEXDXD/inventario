package com.inventario.licoreria.modules.inventory.service;

import com.inventario.licoreria.modules.inventory.dto.TransactionDTO;
import com.inventario.licoreria.modules.inventory.model.Transaction;
import com.inventario.licoreria.modules.inventory.repository.TransactionRepository;
import com.inventario.licoreria.modules.products.model.Product;
import com.inventario.licoreria.modules.products.service.ProductService;
import com.inventario.licoreria.modules.users.model.User;
import com.inventario.licoreria.modules.users.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.lang.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
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
        logger.info("🔄 [CREATE TRANSACTION] Iniciando creación de transacción: productId={}, type={}, quantity={}, userId={}",
            dto.getProductId(), dto.getType(), dto.getQuantity(), dto.getUserId());
        
        try {
            final Product product = productService.findById(dto.getProductId());
            logger.info("✅ [CREATE TRANSACTION] Producto encontrado: {} (ID: {})", product.getName(), product.getId());
            
            final User user = userService.findById(dto.getUserId());
            logger.info("✅ [CREATE TRANSACTION] Usuario encontrado: {} (ID: {})", user.getUsername(), user.getId());

            final String tipo = dto.getType();
            if (!"ENTRADA".equalsIgnoreCase(tipo) && !"SALIDA".equalsIgnoreCase(tipo)) {
                logger.error("❌ [CREATE TRANSACTION] Tipo inválido: {}", tipo);
                throw new RuntimeException("Tipo de transacción inválido: " + tipo);
            }
            
            final int stockDelta;
            if ("ENTRADA".equalsIgnoreCase(tipo)) {
                stockDelta = dto.getQuantity();
                logger.info("📦 [CREATE TRANSACTION] ENTRADA: stock delta = +{}", stockDelta);
            } else {
                stockDelta = -dto.getQuantity();
                logger.info("📤 [CREATE TRANSACTION] SALIDA: stock delta = {}", stockDelta);
            }
            
            productService.adjustStock(product.getId(), stockDelta);
            logger.info("✅ [CREATE TRANSACTION] Stock actualizado de {} a {}", product.getStock(), product.getStock() + stockDelta);
            
            final Transaction transaction = new Transaction();
            transaction.setProduct(product);
            transaction.setType(tipo.toUpperCase());
            transaction.setQuantity(dto.getQuantity());
            transaction.setDateTime(dto.getDateTime() != null ? dto.getDateTime() : LocalDateTime.now());
            transaction.setUser(user);
            
            logger.info("💾 [CREATE TRANSACTION] Guardando transacción en BD...");
            final Transaction saved = transactionRepository.save(transaction);
            logger.info("✅ [CREATE TRANSACTION] Transacción guardada exitosamente: ID = {}", saved.getId());
            
            return saved;
        } catch (RuntimeException e) {
            logger.error("❌ [CREATE TRANSACTION] Error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("❌ [CREATE TRANSACTION] Error inesperado: {}", e.getMessage(), e);
            throw new RuntimeException("Error al crear transacción: " + e.getMessage());
        }
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