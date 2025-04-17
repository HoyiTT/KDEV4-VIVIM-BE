package com.welcommu.moduleservice.logging;

import com.welcommu.moduledomain.company.Company;
import com.welcommu.moduledomain.logging.AuditLog;
import com.welcommu.moduledomain.logging.enums.ActionType;
import com.welcommu.moduledomain.logging.enums.TargetType;
import com.welcommu.modulerepository.logging.AuditLogRepository;
import com.welcommu.moduleservice.logging.dto.CompanySnapshot;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CompanyAuditLog{

    private static final Logger log = LoggerFactory.getLogger(CompanyAuditLog.class);
    private final AuditLogRepository auditLogRepository;
    private final AuditLogFieldComparator auditLogFieldComparator;


    public void createAuditLog(CompanySnapshot dto, Long userId) {
        AuditLog log = AuditLog.builder()
            .actorId(userId)
            .targetType(TargetType.COMPANY)
            .targetId(dto.getId())
            .actionType(ActionType.CREATE)
            .loggedAt(LocalDateTime.now())
            .build();
        auditLogRepository.save(log);
    }


    public void modifyAuditLog(CompanySnapshot before, CompanySnapshot after, Long userId) {
        Map<String, String[]> changedFields = auditLogFieldComparator.compare(before, after);

        AuditLog log = AuditLog.builder()
            .actorId(userId)
            .targetType(TargetType.COMPANY)
            .targetId(after.getId())
            .actionType(ActionType.MODIFY)
            .loggedAt(LocalDateTime.now())
            .build();

        CompanyAuditLog.log.info("log {}", log);
        changedFields.forEach((field, values) ->
            log.addDetail(field, values[0], values[1])
        );

        auditLogRepository.save(log);
    }

    public void deleteAuditLog(CompanySnapshot dto, Long userId) {
        AuditLog log = AuditLog.builder()
            .actorId(userId)
            .targetType(TargetType.COMPANY)
            .targetId(dto.getId())
            .actionType(ActionType.DELETE)
            .loggedAt(LocalDateTime.now())
            .build();
        auditLogRepository.save(log);
    }
}
