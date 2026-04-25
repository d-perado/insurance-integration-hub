package com.insurance.integrationhub.repository;

import com.insurance.integrationhub.domain.ExecutionLog;
import com.insurance.integrationhub.domain.FailureType;
import com.insurance.integrationhub.domain.InterfaceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExecutionLogRepository extends JpaRepository<ExecutionLog, Long> {

    List<ExecutionLog> findTop5ByInterfaceSystemIdOrderByExecutedAtDesc(Long interfaceSystemId);

    List<ExecutionLog> findAllByOrderByExecutedAtDesc();

    List<ExecutionLog> findByInterfaceSystemIdOrderByExecutedAtDesc(Long interfaceSystemId);

    List<ExecutionLog> findByStatusOrderByExecutedAtDesc(InterfaceStatus status);

    List<ExecutionLog> findByFailureTypeOrderByExecutedAtDesc(FailureType failureType);

    @Query("""
            select e
            from ExecutionLog e
            where (:interfaceId is null or e.interfaceSystem.id = :interfaceId)
              and (:status is null or e.status = :status)
              and (:failureType is null or e.failureType = :failureType)
            order by e.executedAt desc
            """)
    Page<ExecutionLog> findExecutionLogs(
            Long interfaceId,
            InterfaceStatus status,
            FailureType failureType,
            Pageable pageable
    );
}