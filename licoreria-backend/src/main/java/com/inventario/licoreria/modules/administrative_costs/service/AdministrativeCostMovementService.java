package com.inventario.licoreria.modules.administrative_costs.service;

import com.inventario.licoreria.modules.administrative_costs.dto.AdministrativeCostMovementDTO;
import com.inventario.licoreria.modules.administrative_costs.model.AdministrativeCost;
import com.inventario.licoreria.modules.administrative_costs.model.AdministrativeCostMovement;
import com.inventario.licoreria.modules.administrative_costs.repository.AdministrativeCostMovementRepository;
import com.inventario.licoreria.modules.users.model.User;
import com.inventario.licoreria.modules.users.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import org.springframework.lang.NonNull;

@Service
public class AdministrativeCostMovementService {

    private final AdministrativeCostMovementRepository movementRepository;
    private final AdministrativeCostService administrativeCostService;
    private final UserService userService;

    public AdministrativeCostMovementService(
            AdministrativeCostMovementRepository movementRepository,
            AdministrativeCostService administrativeCostService,
            UserService userService) {
        this.movementRepository = movementRepository;
        this.administrativeCostService = administrativeCostService;
        this.userService = userService;
    }

    private void validateUserOwnsStore(@NonNull Long storeId, @NonNull String username) {
        administrativeCostService.findByStoreId(storeId, username);
    }

    private void validateUserOwnsMovement(@NonNull Long movementId, @NonNull String username) {
        AdministrativeCostMovement movement = findById(movementId);
        if (!movement.getAdministrativeCost().getStore().getManager().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "No tienes permiso para modificar este movimiento");
        }
    }

    public List<AdministrativeCostMovement> findAll() {
        return movementRepository.findAll();
    }

    public List<AdministrativeCostMovement> findAllByUsername(@NonNull String username) {
        return movementRepository.findAll().stream()
                .filter(movement -> movement.getAdministrativeCost().getStore().getManager().getUsername().equals(username))
                .toList();
    }

    public List<AdministrativeCostMovement> findByStoreId(@NonNull Long storeId) {
        return movementRepository.findByStoreId(storeId);
    }

    public List<AdministrativeCostMovement> findByStoreId(@NonNull Long storeId, @NonNull String username) {
        validateUserOwnsStore(storeId, username);
        return findByStoreId(storeId);
    }

    public List<AdministrativeCostMovement> findByCostId(@NonNull Long costId, @NonNull String username) {
        AdministrativeCost cost = administrativeCostService.findById(costId);
        if (!cost.getStore().getManager().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "No tienes permiso para ver estos movimientos");
        }
        return movementRepository.findByCostId(costId);
    }

    @org.springframework.lang.NonNull
    @SuppressWarnings("null")
    public AdministrativeCostMovement findById(@NonNull Long id) {
        return movementRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movimiento no encontrado"));
    }

    public AdministrativeCostMovement create(final AdministrativeCostMovementDTO dto, @NonNull String username) {
        AdministrativeCost cost = administrativeCostService.findById(dto.getAdministrativeCostId());
        validateUserOwnsStore(cost.getStore().getId(), username);
        
        // Obtener el usuario del username (del JWT)
        User user = userService.findByUsername(username);
        
        final AdministrativeCostMovement movement = new AdministrativeCostMovement();
        movement.setAdministrativeCost(cost);
        movement.setType(dto.getType());
        movement.setAmountPaid(dto.getAmountPaid());
        movement.setDateTime(dto.getDateTime());
        movement.setUser(user);
        
        return movementRepository.save(movement);
    }

    public AdministrativeCostMovement update(@NonNull Long id, final AdministrativeCostMovementDTO dto, @NonNull String username) {
        validateUserOwnsMovement(id, username);
        
        AdministrativeCost cost = administrativeCostService.findById(dto.getAdministrativeCostId());
        validateUserOwnsStore(cost.getStore().getId(), username);
        
        // Obtener el usuario del username (del JWT)
        User user = userService.findByUsername(username);
        
        final AdministrativeCostMovement movement = findById(id);
        movement.setAdministrativeCost(cost);
        movement.setType(dto.getType());
        movement.setAmountPaid(dto.getAmountPaid());
        movement.setDateTime(dto.getDateTime());
        movement.setUser(user);
        
        return movementRepository.save(movement);
    }

    public void delete(@NonNull Long id, @NonNull String username) {
        validateUserOwnsMovement(id, username);
        movementRepository.deleteById(id);
    }
}
