package com.codeflow.toolflow.persistence.tool.repository;

import com.codeflow.toolflow.persistence.tool.entity.Tool;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * Utility class containing specifications for the {@link Tool} entity
 * to support dynamic filtering and searching in the database.
 */
public class ToolSpecifications {

    /**
     * Filters tools with status=true (active).
     *
     * @return a {@link Specification} filtering only active tools
     */
    public static Specification<Tool> toolIsActive() {
        return (root, query, cb) -> cb.equal(root.get("status"), true);
    }

    /**
     * Performs a search for multiple values on a specific column of the {@link Tool} entity.
     * <p>
     * If the column is nested (e.g., "category.name"), it will create a join to the related entity.
     * For text-based columns such as "toolName" or "brand", it performs a case-insensitive partial match.
     * For other types, it performs an exact match using the SQL IN clause.
     *
     * @param column the column name to filter by (supports nested properties with dot notation)
     * @param values the list of values to search for
     * @return a {@link Specification} representing the filtering condition
     */
    public static Specification<Tool> searchByColumnValues(String column, List<String> values) {
        return (root, query, cb) -> {
            if (column.contains(".")) {
                String[] parts = column.split("\\.");
                Join<Object, Object> join = root.join(parts[0]);
                return join.get(parts[1]).in(values);
            } else {
                if (List.of("toolName", "brand").contains(column)) {
                    List<Predicate> predicates = values.stream()
                            .map(value -> cb.like(cb.lower(root.get(column)), "%" + value.toLowerCase() + "%"))
                            .toList();
                    return cb.or(predicates.toArray(new Predicate[0]));
                } else {
                    return root.get(column).in(values);
                }
            }
        };
    }
}
