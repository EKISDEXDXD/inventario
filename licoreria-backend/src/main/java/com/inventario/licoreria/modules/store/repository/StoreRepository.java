package com.inventario.licoreria.modules.store.repository;

import com.inventario.licoreria.modules.store.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
