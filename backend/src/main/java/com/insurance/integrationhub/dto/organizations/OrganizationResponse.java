package com.insurance.integrationhub.dto.organizations;

import com.insurance.integrationhub.domain.ExternalOrganization;

import java.time.LocalDateTime;

public record OrganizationResponse(
        Long id,
        String name,
        String managerName,
        String managerEmail,
        LocalDateTime createdAt
) {
    public static OrganizationResponse from(ExternalOrganization organization) {
        return new OrganizationResponse(
                organization.getId(),
                organization.getName(),
                organization.getManagerName(),
                organization.getManagerEmail(),
                organization.getCreatedAt()
        );
    }
}