package com.inventario.licoreria.modules.store.repository;

import com.inventario.licoreria.modules.store.model.Store;
import com.inventario.licoreria.modules.users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findByManager(User manager);
    Store findByName(String name);
}
