package com.codeflow.toolflow.dto.tool;

import com.codeflow.toolflow.dto.category.CategoryResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * Data Transfer Object (DTO) representing the response data for a tool.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ToolResponse implements Serializable {

    /**
     * The unique identifier of the tool.
     */
    private Long id;

    /**
     * The name of the tool.
     */
    private String toolName;

    /**
     * The brand or manufacturer of the tool.
     */
    private String brand;

    /**
     * The total number of units available in inventory.
     */
    private Integer quantity;

    /**
     * The number of units currently available for use.
     */
    private Integer available;

    /**
     * The number of units currently marked as damaged.
     */
    private Integer damaged;

    /**
     * The number of units currently on loan.
     */
    private Integer onLoan;

    /**
     * Indicates whether the tool is consumable (i.e., not returned after use).
     */
    private Boolean consumable;

    /**
     * The minimum quantity required in stock before triggering an alert.
     */
    private Integer minimalRegistration;

    /**
     * The current status of the tool (true = active, false = inactive).
     */
    private Boolean status;

    /**
     * The timestamp when the tool was created.
     */
    private LocalDateTime createdAt;

    /**
     * The timestamp when the tool was last updated.
     */
    private LocalDateTime updatedAt;

    /**
     * The category to which this tool belongs.
     */
    private CategoryResponse category;
}
