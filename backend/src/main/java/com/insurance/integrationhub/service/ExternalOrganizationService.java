package com.insurance.integrationhub.service;

import com.insurance.integrationhub.common.exception.ServiceErrorCode;
import com.insurance.integrationhub.common.exception.ServiceException;
import com.insurance.integrationhub.domain.ExternalOrganization;
import com.insurance.integrationhub.dto.organizations.CreateOrganizationRequest;
import com.insurance.integrationhub.dto.organizations.OrganizationResponse;
import com.insurance.integrationhub.dto.organizations.UpdateOrganizationRequest;
import com.insurance.integrationhub.repository.ExternalOrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExternalOrganizationService {

    private final ExternalOrganizationRepository externalOrganizationRepository;

    @Transactional
    public OrganizationResponse createOrganization(CreateOrganizationRequest request) {
        validateDuplicateName(request.name());

        ExternalOrganization organization = new ExternalOrganization(
                request.name(),
                request.managerName(),
                request.managerEmail()
        );

        ExternalOrganization savedOrganization =
                externalOrganizationRepository.save(organization);

        return OrganizationResponse.from(savedOrganization);
    }

    public List<OrganizationResponse> getOrganizations() {
        return externalOrganizationRepository.findAll()
                .stream()
                .map(OrganizationResponse::from)
                .toList();
    }

    public OrganizationResponse getOrganization(Long organizationId) {
        ExternalOrganization organization = findOrganization(organizationId);

        return OrganizationResponse.from(organization);
    }

    @Transactional
    public OrganizationResponse updateOrganization(
            Long organizationId,
            UpdateOrganizationRequest request
    ) {
        ExternalOrganization organization = findOrganization(organizationId);

        if (!organization.getName().equals(request.name())) {
            validateDuplicateName(request.name());
        }

        organization.update(
                request.name(),
                request.managerName(),
                request.managerEmail()
        );

        return OrganizationResponse.from(organization);
    }

    @Transactional
    public void deleteOrganization(Long organizationId) {
        ExternalOrganization organization = findOrganization(organizationId);

        externalOrganizationRepository.delete(organization);
    }

    private ExternalOrganization findOrganization(Long organizationId) {
        return externalOrganizationRepository.findById(organizationId)
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.ORGANIZATION_NOT_FOUND));
    }

    private void validateDuplicateName(String name) {
        if (externalOrganizationRepository.existsByName(name)) {
            throw new ServiceException(ServiceErrorCode.ORGANIZATION_NAME_DUPLICATED);
        }
    }
}