package com.insurance.integrationhub.repository;

import com.insurance.integrationhub.domain.ExecutionLog;
import com.insurance.integrationhub.domain.FailureType;
import com.insurance.integrationhub.domain.InterfaceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExecutionLogRepository extends JpaRepository<ExecutionLog, Long> {

    List<ExecutionLog> findTop5ByInterfaceSystemIdOrderByExecutedAtDesc(Long interfaceSystemId);

    List<ExecutionLog> findAllByOrderByExecutedAtDesc();

    List<ExecutionLog> findByInterfaceSystemIdOrderByExecutedAtDesc(Long interfaceSystemId);

    List<ExecutionLog> findByStatusOrderByExecutedAtDesc(InterfaceStatus status);

    List<ExecutionLog> findByFailureTypeOrderByExecutedAtDesc(FailureType failureType);
}