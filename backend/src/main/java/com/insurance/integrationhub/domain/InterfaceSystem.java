package com.insurance.integrationhub.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "interface_systems",
        indexes = {
                @Index(name = "idx_interface_status", columnList = "status"),
                @Index(name = "idx_interface_protocol_type", columnList = "protocol_type"),
                @Index(name = "idx_interface_organization", columnList = "external_organization_id")
        }
)
public class InterfaceSystem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 단방향 ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "external_organization_id", nullable = false)
    private ExternalOrganization externalOrganization;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProtocolType protocolType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterfaceStatus status;

    @Column(nullable = false)
    private String ownerTeam;

    @Column(nullable = false)
    private String endpoint;

    @Column(length = 1000)
    private String description;

    private LocalDateTime lastExecutedAt;

    private Integer avgResponseMs;

    private Integer todayCount;

    public InterfaceSystem(
            ExternalOrganization externalOrganization,
            String name,
            ProtocolType protocolType,
            InterfaceStatus status,
            String ownerTeam,
            String endpoint,
            String description,
            LocalDateTime lastExecutedAt,
            Integer avgResponseMs,
            Integer todayCount
    ) {
        this.externalOrganization = externalOrganization;
        this.name = name;
        this.protocolType = protocolType;
        this.status = status;
        this.ownerTeam = ownerTeam;
        this.endpoint = endpoint;
        this.description = description;
        this.lastExecutedAt = lastExecutedAt;
        this.avgResponseMs = avgResponseMs;
        this.todayCount = todayCount;
    }

    public void updateStatus(InterfaceStatus status) {
        this.status = status;
        this.lastExecutedAt = LocalDateTime.now();
    }

    public void updateMetrics(Integer responseTimeMs) {
        this.avgResponseMs = responseTimeMs;
        this.todayCount = this.todayCount == null ? 1 : this.todayCount + 1;
    }
}