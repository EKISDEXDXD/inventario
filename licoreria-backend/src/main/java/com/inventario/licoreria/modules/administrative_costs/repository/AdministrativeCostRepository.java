package com.inventario.licoreria.modules.administrative_costs.repository;

import com.inventario.licoreria.modules.administrative_costs.model.AdministrativeCost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface AdministrativeCostRepository extends JpaRepository<AdministrativeCost, Long> {
    
    @Query("SELECT ac FROM AdministrativeCost ac WHERE ac.store.id = :storeId ORDER BY ac.name ASC")
    List<AdministrativeCost> findByStoreId(@Param("storeId") Long storeId);
}
