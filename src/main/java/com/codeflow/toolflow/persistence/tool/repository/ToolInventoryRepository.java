package com.codeflow.toolflow.persistence.tool.repository;

import com.codeflow.toolflow.persistence.tool.entity.ToolInventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ToolInventoryRepository extends JpaRepository<ToolInventory, Long> {

}
