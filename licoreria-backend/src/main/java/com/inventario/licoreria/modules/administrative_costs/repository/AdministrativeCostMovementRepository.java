package com.inventario.licoreria.modules.administrative_costs.repository;

import com.inventario.licoreria.modules.administrative_costs.model.AdministrativeCostMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface AdministrativeCostMovementRepository extends JpaRepository<AdministrativeCostMovement, Long> {
    
    @Query("SELECT acm FROM AdministrativeCostMovement acm WHERE acm.administrativeCost.id = :costId ORDER BY acm.dateTime DESC")
    List<AdministrativeCostMovement> findByCostId(@Param("costId") Long costId);

    @Query("SELECT acm FROM AdministrativeCostMovement acm WHERE acm.administrativeCost.store.id = :storeId ORDER BY acm.dateTime DESC")
    List<AdministrativeCostMovement> findByStoreId(@Param("storeId") Long storeId);
}
