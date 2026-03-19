package com.finances.finances_api.audit;

import java.util.UUID;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.finances.finances_api.domain.User;
import com.finances.finances_api.dto.auth.AuthResponse;
import com.finances.finances_api.security.UserMain;

@Aspect
@Component
public class AuditAspect {
    private final AuditService auditService;

    public AuditAspect(AuditService auditService) {
        this.auditService = auditService;
    }

    @AfterReturning(value = "@annotation(audited)", returning = "result")
    public void audit(JoinPoint joinPoint, Audited audited, Object result) {
        User user = getAuthenticatedUser();
        if (user == null)
            return;

        UUID entityId = extractEntityId(joinPoint.getArgs());

        if (entityId == null && result instanceof AuthResponse ar) {
            entityId = ar.getUserId();
        }

        auditService.log(user, audited.action(), audited.entity(), entityId);
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserMain)) {
            return null;
        }
        return ((UserMain) auth.getPrincipal()).getUser();
    }

    private UUID extractEntityId(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof UUID) {
                return (UUID) arg;
            }
        }
        return null;
    }
}
