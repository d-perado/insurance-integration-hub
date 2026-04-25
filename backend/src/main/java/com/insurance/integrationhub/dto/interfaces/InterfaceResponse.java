package com.insurance.integrationhub.dto.interfaces;

import com.insurance.integrationhub.domain.InterfaceStatus;
import com.insurance.integrationhub.domain.InterfaceSystem;
import com.insurance.integrationhub.domain.ProtocolType;

import java.time.LocalDateTime;

public record InterfaceResponse(
        Long id,
        String name,
        String organization,
        ProtocolType protocol,
        InterfaceStatus status,
        String ownerTeam,
        String endpoint,
        String description,
        LocalDateTime lastExecutedAt,
        Integer avgResponseMs,
        Integer todayCount
) {
    public static InterfaceResponse from(InterfaceSystem interfaceSystem) {
        return new InterfaceResponse(
                interfaceSystem.getId(),
                interfaceSystem.getName(),
                interfaceSystem.getExternalOrganization().getName(),
                interfaceSystem.getProtocolType(),
                interfaceSystem.getStatus(),
                interfaceSystem.getOwnerTeam(),
                interfaceSystem.getEndpoint(),
                interfaceSystem.getDescription(),
                interfaceSystem.getLastExecutedAt(),
                interfaceSystem.getAvgResponseMs(),
                interfaceSystem.getTodayCount()
        );
    }
}