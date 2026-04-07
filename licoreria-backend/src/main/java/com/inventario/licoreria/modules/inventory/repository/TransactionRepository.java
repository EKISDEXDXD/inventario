package com.inventario.licoreria.modules.inventory.repository;

import com.inventario.licoreria.modules.inventory.model.Transaction;
import java.util.List;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    //Buscar todas las transacciones de un producto (historial de movimientos)
    List<Transaction> findByProductIdOrderByDateTimeDesc(Long productId);

    //Buscar transacciones por rango de fechas (para reportes diarios, mensuales, etc.)
    List<Transaction> findByDateTimeBetweenOrderByDateTimeDesc(LocalDateTime start, LocalDateTime end);

    // Buscar por tipo (ENTRADA/SALIDA)
    List<Transaction> findByTypeOrderByDateTimeDesc(String type);

    // Buscar por ID de usuario (quién realizó la venta/compra)
    List<Transaction> findByUserIdOrderByDateTimeDesc(Long userId);
}
