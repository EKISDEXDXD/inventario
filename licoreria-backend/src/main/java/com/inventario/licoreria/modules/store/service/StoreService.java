package com.inventario.licoreria.modules.store.service;

import com.inventario.licoreria.modules.store.dto.StoreCreateDTO;
import com.inventario.licoreria.modules.store.dto.StoreResponseDTO;
import com.inventario.licoreria.modules.store.dto.StoreUpdateDTO;
import com.inventario.licoreria.modules.store.model.Store;
import com.inventario.licoreria.modules.store.repository.StoreRepository;
import com.inventario.licoreria.modules.users.model.User;
import com.inventario.licoreria.modules.users.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public StoreService(StoreRepository storeRepository, UserService userService) {
        this.storeRepository = storeRepository;
        this.userService = userService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public List<StoreResponseDTO> findAllByUser(String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario no encontrado");
        }
        return storeRepository.findByManager(user).stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    public StoreResponseDTO findById(Long id, String username) {
        Store store = findStoreById(id);
        // Permitir que cualquier usuario autenticado vea cualquier tienda (acceso en lectura)
        return convertToResponseDTO(store);
    }

    public StoreResponseDTO create(StoreCreateDTO dto, String username) {
        if (storeRepository.findAll().stream().anyMatch(store -> store.getName().equalsIgnoreCase(dto.getName()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de tienda ya existe");
        }

        User manager = userService.findByUsername(username);
        if (manager == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El encargado no existe");
        }

        Store store = new Store();
        store.setName(dto.getName());
        store.setDescription(dto.getDescription());
        store.setAddress(dto.getAddress());
        store.setAccessPassword(passwordEncoder.encode(dto.getAccessPassword()));
        store.setManager(manager);

        Store saved = storeRepository.save(store);
        return convertToResponseDTO(saved);
    }

    public StoreResponseDTO update(Long id, StoreUpdateDTO dto, String username) {
        try {
            log.info("Iniciando actualización de tienda {} por usuario {}", id, username);
            log.debug("DTO recibido: name='{}', password='{}'", dto.getName(), dto.getAccessPassword() != null ? "***" : "null");
            
            Store store = findStoreById(id);
            
            // Validar que el usuario autenticado sea el propietario de la tienda
            if (!store.getManager().getUsername().equals(username)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para editar esta tienda");
            }
            
            // Validar que el nuevo nombre no esté vacío
            if (dto.getName() == null || dto.getName().trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de la tienda no puede estar vacío");
            }
            
            // Validar que el nuevo nombre no exista (excepto el de la tienda actual)
            String newName = dto.getName().trim();
            if (!store.getName().equalsIgnoreCase(newName) && 
                storeRepository.findAll().stream().anyMatch(s -> s.getName().equalsIgnoreCase(newName))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de tienda ya existe");
            }
            
            // Actualizar nombre
            store.setName(newName);
            
            // Actualizar contraseña solo si se proporciona
            String password = dto.getAccessPassword();
            if (password != null && !password.trim().isEmpty()) {
                // Validar longitud de contraseña
                if (password.trim().length() < 6 || password.trim().length() > 50) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe tener entre 6 y 50 caracteres");
                }
                store.setAccessPassword(passwordEncoder.encode(password.trim()));
            }
            
            Store updated = storeRepository.save(store);
            log.info("Tienda {} actualizada exitosamente", id);
            return convertToResponseDTO(updated);
        } catch (ResponseStatusException e) {
            log.warn("Error de validación al actualizar tienda {}: {}", id, e.getReason());
            throw e;
        } catch (Exception e) {
            log.error("Error al actualizar tienda " + id, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar la tienda: " + e.getMessage());
        }
    }

    public void delete(Long id, String username) {
        Store store = findStoreById(id);
        if (!store.getManager().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes eliminar una tienda que no es tuya");
        }
        storeRepository.delete(store);
    }

    private Store findStoreById(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tienda no encontrada"));
    }

    public Store findStoreEntity(Long id) {
        return findStoreById(id);
    }

    public StoreResponseDTO accessExternal(String storeName, String password) {
        Store store = storeRepository.findByName(storeName);
        if (store == null || !passwordEncoder.matches(password, store.getAccessPassword())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tienda no encontrada o contraseña incorrecta");
        }
        StoreResponseDTO dto = convertToResponseDTO(store);
        dto.setExternal(true);
        return dto;
    }

    public Store findStoreByIdExternal(Long id) {
        return findStoreById(id);
    }

    public void validateUserAccess(Store store, String username) {
        User user = userService.findByUsername(username);
        if (user == null || !store.getManager().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para acceder a esta tienda");
        }
    }

    private StoreResponseDTO convertToResponseDTO(Store store) {
        return new StoreResponseDTO(
                store.getId(),
                store.getName(),
                store.getDescription(),
                store.getAddress(),
                store.getManager().getId(),
                store.getManager().getUsername()
        );
    }
}
