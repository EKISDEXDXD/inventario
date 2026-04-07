package com.inventario.licoreria.modules.users.controller;

import com.inventario.licoreria.modules.users.dto.UpdateUserDTO;
import com.inventario.licoreria.modules.users.dto.UserCreateDTO;
import com.inventario.licoreria.modules.users.dto.UserResponseDTO;
import com.inventario.licoreria.modules.users.model.User;
import com.inventario.licoreria.modules.users.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Objects;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponseDTO> getAll() {
        return userService.findAll();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(Authentication authentication) {
        Objects.requireNonNull(authentication, "authentication no puede ser null");
        String username = Objects.requireNonNull(authentication.getName(), "username no puede ser null");
        User user = Objects.requireNonNull(userService.findByUsername(username), "Usuario no encontrado");
        UserResponseDTO userResponse = userService.findByIdDto(Objects.requireNonNull(user.getId(), "id de usuario no puede ser null"));
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getById(@PathVariable Long id) {
        Long nonNullId = Objects.requireNonNull(id, "id no puede ser null");
        return ResponseEntity.ok(userService.findByIdDto(nonNullId));
    }

    @PostMapping
    public UserResponseDTO create(@Valid @RequestBody UserCreateDTO dto) {
        return userService.create(dto);
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateCurrentUser(@Valid @RequestBody UpdateUserDTO dto, Authentication authentication) {
        Objects.requireNonNull(authentication, "authentication no puede ser null");
        String username = Objects.requireNonNull(authentication.getName(), "username no puede ser null");
        User user = Objects.requireNonNull(userService.findByUsername(username), "Usuario no encontrado");
        Long userId = Objects.requireNonNull(user.getId(), "id de usuario no puede ser null");
        String newUsername = Objects.requireNonNull(dto.getUsername(), "username no puede ser null");
        return ResponseEntity.ok(userService.updateUsername(userId, newUsername));
    }
}