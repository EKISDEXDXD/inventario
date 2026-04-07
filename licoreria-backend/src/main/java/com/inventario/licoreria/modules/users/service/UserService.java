package com.inventario.licoreria.modules.users.service;

import com.inventario.licoreria.modules.users.dto.UserCreateDTO;
import com.inventario.licoreria.modules.users.dto.UserResponseDTO;
import com.inventario.licoreria.modules.users.model.Role;
import com.inventario.licoreria.modules.users.model.User;
import com.inventario.licoreria.modules.users.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAllModels() {
        return userRepository.findAll();
    }

    public List<UserResponseDTO> findAll() {
        return userRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    public UserResponseDTO findByIdDto(@NonNull final Long id) {
        return convertToResponseDTO(findById(id));
    }

    public UserResponseDTO create(final UserCreateDTO dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de usuario ya existe");
        }
        final User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole() != null ? dto.getRole() : Role.USER);
        User savedUser = userRepository.save(user);
        return convertToResponseDTO(savedUser);
    }

    public User findByUsername(@NonNull final String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));
    }

    public UserResponseDTO updateUsername(@NonNull final Long id, @NonNull final String newUsername) {
        User user = findById(id);
        if (!user.getUsername().equals(newUsername) && userRepository.findByUsername(newUsername).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de usuario ya existe");
        }
        user.setUsername(newUsername);
        User updatedUser = userRepository.save(user);
        return convertToResponseDTO(updatedUser);
    }

    public User findById(@NonNull final Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    public void delete(@NonNull final Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserResponseDTO convertToResponseDTO(final User user) {
        return new UserResponseDTO(user.getId(),
                                 user.getUsername(),
                                 user.getRole().name());
    }
}
