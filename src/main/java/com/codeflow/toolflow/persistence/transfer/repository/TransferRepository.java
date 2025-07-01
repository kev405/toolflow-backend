package com.codeflow.toolflow.persistence.transfer.repository;

import com.codeflow.toolflow.persistence.transfer.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link Transfer} entities.
 */
@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {
}
