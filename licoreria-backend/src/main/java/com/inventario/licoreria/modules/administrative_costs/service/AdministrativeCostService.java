package com.inventario.licoreria.modules.administrative_costs.service;

import com.inventario.licoreria.modules.administrative_costs.dto.AdministrativeCostDTO;
import com.inventario.licoreria.modules.administrative_costs.model.AdministrativeCost;
import com.inventario.licoreria.modules.administrative_costs.repository.AdministrativeCostRepository;
import com.inventario.licoreria.modules.store.model.Store;
import com.inventario.licoreria.modules.store.service.StoreService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import org.springframework.lang.NonNull;

@Service
public class AdministrativeCostService {

    private final AdministrativeCostRepository administrativeCostRepository;
    private final StoreService storeService;

    public AdministrativeCostService(AdministrativeCostRepository administrativeCostRepository, StoreService storeService) {
        this.administrativeCostRepository = administrativeCostRepository;
        this.storeService = storeService;
    }

    private void validateUserOwnsStore(@NonNull Long storeId, @NonNull String username) {
        Store store = storeService.findStoreEntity(storeId);
        if (!store.getManager().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "No tienes permiso para acceder a los costos administrativos de esta tienda");
        }
    }

    private void validateUserOwnsCost(@NonNull Long costId, @NonNull String username) {
        AdministrativeCost cost = findById(costId);
        if (!cost.getStore().getManager().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "No tienes permiso para modificar este costo administrativo");
        }
    }

    public List<AdministrativeCost> findAll() {
        return administrativeCostRepository.findAll();
    }

    public List<AdministrativeCost> findAllByUsername(@NonNull String username) {
        return administrativeCostRepository.findAll().stream()
                .filter(cost -> cost.getStore().getManager().getUsername().equals(username))
                .toList();
    }

    public List<AdministrativeCost> findByStoreId(@NonNull Long storeId) {
        return administrativeCostRepository.findByStoreId(storeId);
    }

    public List<AdministrativeCost> findByStoreId(@NonNull Long storeId, @NonNull String username) {
        validateUserOwnsStore(storeId, username);
        return findByStoreId(storeId);
    }

    @org.springframework.lang.NonNull
    @SuppressWarnings("null")
    public AdministrativeCost findById(@NonNull Long id) {
        return administrativeCostRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Costo administrativo no encontrado"));
    }

    public AdministrativeCost create(final AdministrativeCostDTO dto, @NonNull String username) {
        validateUserOwnsStore(dto.getStoreId(), username);
        final AdministrativeCost cost = new AdministrativeCost();
        cost.setName(dto.getName());
        cost.setDescription(dto.getDescription());
        cost.setCost(dto.getCost());
        cost.setStore(storeService.findStoreEntity(dto.getStoreId()));
        return administrativeCostRepository.save(cost);
    }

    public AdministrativeCost update(@NonNull Long id, final AdministrativeCostDTO dto, @NonNull String username) {
        validateUserOwnsCost(id, username);
        validateUserOwnsStore(dto.getStoreId(), username);
        
        final AdministrativeCost cost = findById(id);
        cost.setName(dto.getName());
        cost.setDescription(dto.getDescription());
        cost.setCost(dto.getCost());
        return administrativeCostRepository.save(cost);
    }

    public void delete(@NonNull Long id, @NonNull String username) {
        validateUserOwnsCost(id, username);
        administrativeCostRepository.deleteById(id);
    }
}
