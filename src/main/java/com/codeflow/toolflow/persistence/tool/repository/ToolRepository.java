package com.codeflow.toolflow.persistence.tool.repository;

import com.codeflow.toolflow.persistence.tool.entity.Tool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Tool} entities in the database.
 * <p>
 * This interface provides standard JPA-based operations such as saving, deleting,
 * and finding tools. It also includes support for dynamic queries using specifications,
 * allowing more flexible and complex filtering.
 * <p>
 * It extends {@link JpaRepository} to gain access to built-in JPA functionality,
 * such as pagination and sorting, and {@link JpaSpecificationExecutor} to support
 * custom queries based on specifications.
 */
@Repository
public interface ToolRepository extends JpaRepository<Tool, Long>, JpaSpecificationExecutor<Tool> {
}
