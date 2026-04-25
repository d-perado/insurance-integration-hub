package com.insurance.integrationhub.repository;

import com.insurance.integrationhub.domain.InterfaceStatus;
import com.insurance.integrationhub.domain.InterfaceSystem;
import com.insurance.integrationhub.domain.ProtocolType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterfaceSystemRepository extends JpaRepository<InterfaceSystem, Long> {

    List<InterfaceSystem> findByStatus(InterfaceStatus status);

    List<InterfaceSystem> findByProtocolType(ProtocolType protocolType);

    List<InterfaceSystem> findByNameContaining(String keyword);

    boolean existsByName(String name);
}