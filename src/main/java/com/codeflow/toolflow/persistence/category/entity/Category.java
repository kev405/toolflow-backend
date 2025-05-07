package com.codeflow.toolflow.persistence.category.entity;

import com.codeflow.toolflow.persistence.tool.entity.Tool;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static jakarta.persistence.GenerationType.SEQUENCE;

/**
 * Represents a category of tools in the system.
 * A category groups tools with common characteristics, such as function or type.
 */
@Entity
@Table(name = "tool_category")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Category {

    /**
     * The sequence name used for the `id` field in the `Category` entity.
     * Defines the database sequence `tool_category_id_seq` used for generating unique identifiers.
     */
    public static final String ID_SEQ = "tool_category_id_seq";

    /**
     * Represents the unique identifier for a category.
     * Automatically generated using a database sequence.
     */
    @Id
    @GeneratedValue(generator = ID_SEQ, strategy = SEQUENCE)
    @SequenceGenerator(name = ID_SEQ, sequenceName = ID_SEQ, allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * The name of the tool category.
     */
    @NotNull
    @Column(unique = true)
    private String name;

    /**
     * Indicates the current status of the category.
     * If true, the category is active and can be assigned to tools.
     */
    @NotNull
    private Boolean status;

    /**
     * The timestamp when the category was created.
     */
    @NotNull
    private LocalDateTime createdAt;

    /**
     * The ID of the user who created this category.
     */
    @NotNull
    private Long createdBy;

    /**
     * The timestamp of the last update made to the category.
     */
    @NotNull
    private LocalDateTime updatedAt;

    /**
     * The ID of the user who last updated this category.
     */
    @NotNull
    private Long updatedBy;

    /**
     * Represents the collection of tools that belong to this category.
     * Defined as a one-to-many relationship where one category can contain multiple tools.
     * Cascade operations and orphan removal ensure consistency when tools are managed via the category.
     */
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tool> tools = new ArrayList<>();
}
