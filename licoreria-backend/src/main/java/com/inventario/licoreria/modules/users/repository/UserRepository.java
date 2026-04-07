package com.inventario.licoreria.modules.users.repository;

import com.inventario.licoreria.modules.users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
