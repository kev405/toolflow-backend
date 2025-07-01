package com.codeflow.toolflow.persistence.transfer.repository;

import com.codeflow.toolflow.persistence.transfer.entity.TransferTool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link TransferTool} entities.
 */
@Repository
public interface TransferToolRepository extends JpaRepository<TransferTool, Long> {
}
