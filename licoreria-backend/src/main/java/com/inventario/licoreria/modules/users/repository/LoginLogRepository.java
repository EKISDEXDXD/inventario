package com.inventario.licoreria.modules.users.repository;

import com.inventario.licoreria.modules.users.model.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {
}