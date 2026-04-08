package com.inventario.licoreria.security;

import com.inventario.licoreria.modules.users.dto.UserCreateDTO;
import com.inventario.licoreria.modules.users.dto.UserResponseDTO;
import com.inventario.licoreria.modules.users.model.LoginLog;
import com.inventario.licoreria.modules.users.model.Role;
import com.inventario.licoreria.modules.users.model.User;
import com.inventario.licoreria.modules.users.repository.LoginLogRepository;
import com.inventario.licoreria.modules.users.service.UserService;

import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final LoginLogRepository loginLogRepository;

    public AuthService(JwtUtil jwtUtil, UserService userService, PasswordEncoder passwordEncoder, LoginLogRepository loginLogRepository) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.loginLogRepository = loginLogRepository;
    }

    public String login(String username, String password) {
        Objects.requireNonNull(username, "username no puede ser null");
        Objects.requireNonNull(password, "password no puede ser null");

        User user = userService.findByUsername(username);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }
        String token = jwtUtil.generateTokenWithRole(username, user.getRole().name());
        loginLogRepository.save(new LoginLog(username));
        return token;
    }

    public UserResponseDTO register(String username, String password) {
        UserCreateDTO dto = new UserCreateDTO();
        dto.setUsername(username);
        dto.setPassword(password);
        dto.setRole(Role.USER);
        return userService.create(dto);
    }
}
