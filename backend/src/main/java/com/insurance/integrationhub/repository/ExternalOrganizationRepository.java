package com.insurance.integrationhub.repository;

import com.insurance.integrationhub.domain.ExternalOrganization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExternalOrganizationRepository extends JpaRepository<ExternalOrganization, Long> {

    boolean existsByName(String name);
}