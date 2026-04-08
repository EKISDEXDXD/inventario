package com.inventario.licoreria.modules.store.service;

import com.inventario.licoreria.modules.store.dto.StoreCreateDTO;
import com.inventario.licoreria.modules.store.dto.StoreResponseDTO;
import com.inventario.licoreria.modules.store.model.Store;
import com.inventario.licoreria.modules.store.repository.StoreRepository;
import com.inventario.licoreria.modules.users.model.User;
import com.inventario.licoreria.modules.users.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserService userService;

    public StoreService(StoreRepository storeRepository, UserService userService) {
        this.storeRepository = storeRepository;
        this.userService = userService;
    }

    public List<StoreResponseDTO> findAll() {
        return storeRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    public StoreResponseDTO findById(Long id) {
        return convertToResponseDTO(findStoreById(id));
    }

    public StoreResponseDTO create(StoreCreateDTO dto) {
        if (storeRepository.findAll().stream().anyMatch(store -> store.getName().equalsIgnoreCase(dto.getName()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de tienda ya existe");
        }

        User manager = userService.findById(dto.getManagerId());
        if (manager == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El encargado no existe");
        }

        Store store = new Store();
        store.setName(dto.getName());
        store.setDescription(dto.getDescription());
        store.setManager(manager);

        Store saved = storeRepository.save(store);
        return convertToResponseDTO(saved);
    }

    private Store findStoreById(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tienda no encontrada"));
    }

    private StoreResponseDTO convertToResponseDTO(Store store) {
        return new StoreResponseDTO(
                store.getId(),
                store.getName(),
                store.getDescription(),
                store.getManager().getId(),
                store.getManager().getUsername()
        );
    }
}
