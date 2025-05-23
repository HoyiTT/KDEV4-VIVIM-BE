package com.welcommu.moduleinfra.logging;

import com.welcommu.moduledomain.logging.AuditLog;
import com.welcommu.moduledomain.logging.enums.ActionType;
import com.welcommu.moduledomain.logging.enums.TargetType;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditLogRepositoryCustom {
    Page<AuditLog> findByConditions(
        ActionType actionType,
        TargetType targetType,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Long userId,
        Pageable pageable
    );

    List<Long> findIdsWithCursor(
        ActionType actionType,
        TargetType targetType,
        LocalDateTime start,
        LocalDateTime end,
        Long userId,
        LocalDateTime cursorLoggedAt,
        Long cursorId,
        int size
    );

    List<AuditLog> findWithDetailsByIds(List<Long> ids);
}