package com.codeflow.toolflow.persistence.tool.entity;

import com.codeflow.toolflow.persistence.category.entity.Category;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static jakarta.persistence.GenerationType.SEQUENCE;

/**
 * Represents a tool in the inventory system.
 * Each tool contains information about its quantity, status, usage, and category association.
 */
@Entity
@Table(name = "tool")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tool {

    /**
     * The sequence name used for the `id` field in the `Tool` entity.
     * This constant  defines the database sequence `tool_id_seq` used to generate unique tool identifiers.
     */
    public static final String ID_SEQ = "tool_id_seq";

    /**
     * Represents the unique identifier for the tool.
     * It is automatically generated using a database sequence strategy.
     */
    @Id
    @GeneratedValue(generator = ID_SEQ, strategy = SEQUENCE)
    @SequenceGenerator(name = ID_SEQ, sequenceName = ID_SEQ, allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * The name or description of the tool.
     * For example: "Cordless Drill", "Welding Helmet", etc.
     * Maximum allowed length is 350 characters.
     */
    @NotNull
    @Column(length = 350)
    private String toolName;

    /**
     * The total quantity of this tool available in inventory.
     */
    @NotNull
    @Column(columnDefinition ="integer default 0")
    private Integer quantity = 0;

    /**
     * The brand or manufacturer of the tool.
     */
    @NotNull
    private String brand;

    /**
     * The number of units currently available for use.
     * This value is updated as tools are loaned or returned.
     */
    @Column(columnDefinition ="integer default 0")
    private Integer available = 0;

    /**
     * The number of tools marked as damaged or not usable.
     */
    @Column(columnDefinition ="integer default 0")
    private Integer damaged = 0;

    /**
     * The number of units currently loaned out to users.
     */
    @Column(columnDefinition ="integer default 0")
    private Integer onLoan = 0;

    /**
     * Optional notes about the tool.
     * For example: condition, accessories included, warnings, etc.
     */
    private String notes;

    /**
     * Indicates whether the tool is consumable.
     * Consumables are items that do not need to be returned, such as gloves or screws.
     */
    private Boolean consumable;

    /**
     * The minimum required quantity before triggering an alert or replenishment request.
     */
    private Integer minimalRegistration;

    /**
     * Indicates whether the tool is active in the system.
     * If false, the tool is considered deactivated or no longer in circulation.
     */
    @NotNull
    private Boolean status;

    /**
     * Timestamp of when the tool record was created.
     */
    @NotNull
    private LocalDateTime createdAt;

    /**
     * The ID of the user who created this tool record.
     */
    @NotNull
    private Long createdBy;

    /**
     * Timestamp of the last update to this tool record.
     */
    @NotNull
    private LocalDateTime updatedAt;

    /**
     * The ID of the user who last updated this tool record.
     */
    @NotNull
    private Long updatedBy;

    /**
     * The category to which this tool belongs.
     * This is a many-to-one relationship, as many tools can belong to a single category.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category", referencedColumnName = "id")
    private Category category;
}
