package com.codeflow.toolflow.persistence.tool.repository;

import com.codeflow.toolflow.persistence.tool.entity.ToolInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ToolInventoryRepository extends JpaRepository<ToolInventory, Long> {

    /**
     * Finds an inventory record by the ID of the tool and the ID of the headquarter.
     *
     * @param toolId The ID of the tool.
     * @param headquarterId The ID of the headquarter.
     * @return An Optional containing the found inventory record, or empty if not found.
     */
    Optional<ToolInventory> findByToolIdAndHeadquarterId(Long toolId, Long headquarterId);
}
