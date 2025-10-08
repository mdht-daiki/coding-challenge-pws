package com.example.procurement.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "audit_log")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private OffsetDateTime occurredAt;

    private UUID actorId;

    @Column(nullable = false, length = 100)
    private String action;

    @Column(nullable = false, length = 100)
    private String targetTable;

    @Column(nullable = false, length = 100)
    private String targetId;

    @Column(nullable = false, length = 64)
    private String hash;

    @Column(length = 64)
    private String prevHash;
}
