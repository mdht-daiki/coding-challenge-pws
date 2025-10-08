package com.example.procurement.repository;

import com.example.procurement.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    AuditLog findTopByOrderByIdDesc();
}
