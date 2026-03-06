package com.finances.finances_api.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finances.finances_api.domain.AuditLog;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    List<AuditLog> findByUserId(UUID userId);

    List<AuditLog> findByEntityNameAndEntityId(String entityName, String entityId);
}
