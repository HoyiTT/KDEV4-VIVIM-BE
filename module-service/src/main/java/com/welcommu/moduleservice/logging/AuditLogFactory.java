package com.welcommu.moduleservice.logging;

import com.welcommu.moduledomain.logging.AuditLog;
import com.welcommu.moduledomain.logging.enums.ActionType;
import com.welcommu.moduledomain.logging.enums.TargetType;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class AuditLogFactory {
    public AuditLog create(TargetType targetType, Long targetId, ActionType actionType, Long actorId) {
        return AuditLog.builder()
            .actorId(actorId)
            .targetType(targetType)
            .targetId(targetId)
            .actionType(actionType)
            .loggedAt(LocalDateTime.now())
            .build();
    }
}
