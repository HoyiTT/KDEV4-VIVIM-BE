package com.welcommu.moduleservice.logging;

import com.welcommu.moduledomain.logging.AuditLog;
import com.welcommu.moduledomain.logging.enums.ActionType;
import com.welcommu.moduledomain.logging.enums.TargetType;
import com.welcommu.moduledomain.user.User;
import com.welcommu.modulerepository.logging.AuditLogRepository;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserAuditService implements AuditableService<User>{
    private final AuditLogRepository auditLogRepository;
    private final AuditLogFieldComparator auditLogFieldComparator;

    @Override
    public void createAuditLog(User entity, Long userId) {
        AuditLog log = AuditLog.builder()
            .actorId(userId)
            .targetType(TargetType.USER)
            .targetId(entity.getId())
            .actionType(ActionType.CREATE)
            .loggedAt(LocalDateTime.now())
            .build();
        auditLogRepository.save(log);
    }

    @Override
    public void modifyAuditLog(User before, User after, Long userId) {
        Map<String, String[]> changedFields = auditLogFieldComparator.compare(before, after);

        AuditLog log = AuditLog.builder()
            .actorId(userId)
            .targetType(TargetType.USER)
            .targetId(after.getId())
            .actionType(ActionType.MODIFY)
            .loggedAt(LocalDateTime.now())
            .build();

        changedFields.forEach((field, values) ->
            log.addDetail(field, values[0], values[1])
        );

        auditLogRepository.save(log);

    }

    @Override
    public void deleteAuditLog(User user, Long userId) {
        AuditLog log = AuditLog.builder()
            .actorId(userId)
            .targetType(TargetType.USER)
            .targetId(user.getId())
            .actionType(ActionType.DELETE)
            .loggedAt(LocalDateTime.now())
            .build();
        auditLogRepository.save(log);
    }
}
