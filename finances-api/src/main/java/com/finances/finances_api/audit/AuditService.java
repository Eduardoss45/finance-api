package com.finances.finances_api.audit;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.finances.finances_api.domain.AuditLog;
import com.finances.finances_api.domain.User;
import com.finances.finances_api.repository.AuditLogRepository;

@Service
public class AuditService {
    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(User user, String action, String entityName, UUID entityId) {
        AuditLog log = new AuditLog();
        log.setUser(user);
        log.setAction(action);
        log.setEntityName(entityName);
        log.setEntityId(entityId);

        auditLogRepository.save(log);
    }
}
