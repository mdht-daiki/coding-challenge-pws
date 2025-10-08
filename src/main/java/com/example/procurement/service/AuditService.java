package com.example.procurement.service;

import com.example.procurement.domain.AuditLog;
import com.example.procurement.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class AuditService {
    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void audit(UUID actorId, String action, String targetTable, String targetId) {
        var prev = auditLogRepository.findTopByOrderByIdDesc();
        var prevHash = prev == null ? null : prev.getHash();

        var now = OffsetDateTime.now();
        var payload = (action + "|" + targetTable + "|" + targetId + "|" + now.toString() + "|" + (prevHash == null ? "" : prevHash));
        var hash = sha256(payload);

        var log = new AuditLog();
        log.setOccurredAt(now);
        log.setActorId(actorId);
        log.setAction(action);
        log.setTargetTable(targetTable);
        log.setTargetId(targetId);
        log.setHash(hash);
        log.setPrevHash(prevHash);

        auditLogRepository.save(log);
    }

    private String sha256(String s) {
        try {
            var md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(s.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
