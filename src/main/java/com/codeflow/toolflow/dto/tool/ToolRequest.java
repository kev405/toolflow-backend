package com.codeflow.toolflow.dto.tool;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * Data Transfer Object (DTO) representing a request for creating or updating a tool.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ToolRequest implements Serializable {

    /**
     * The name of the tool.
     *
     * @NotNull - This field is required.
     * @Size(min = 2, max = 350) - The tool name must be between 2 and 350 characters.
     */
    @NotNull(message = "Tool name is required")
    @Size(min = 2, max = 350, message = "Tool name must be between 2 and 350 characters")
    private String toolName;

    /**
     * The brand or manufacturer of the tool.
     *
     * @NotNull - This field is required.
     */
    @NotNull(message = "Brand is required")
    private String brand;

    /**
     * The number of units currently available for use.
     * This field is optional.
     *
     * @Min(value = 0, message = "Available cannot be negative") - The available quantity must be non-negative.
     */
    @Min(value = 0, message = "Available cannot be negative", groups = {OnCreate.class})
    private Integer available = 0;

    /**
     * The number of units currently marked as damaged or broken.
     * This field is optional.
     *
     * @Min(value = 0, message = "Damaged cannot be negative") - The damaged quantity must be non-negative.
     */
    @Min(value = 0, message = "Damaged cannot be negative", groups = {OnCreate.class})
    private Integer damaged = 0;

    /**
     * The number of units currently on loan (borrowed).
     * This field is optional.
     *
     * @Min(value = 0, message = "On loan cannot be negative") - The on-loan quantity must be non-negative.
     */
    @Min(value = 0, message = "On loan cannot be negative", groups = {OnCreate.class})
    private Integer onLoan = 0;

    /**
     * Additional notes or observations about the tool.
     * For example: includes case, safety manual, accessories, etc.
     * This field is optional.
     */
    private String notes;

    /**
     * Indicates whether the tool is consumable (e.g., gloves, screws).
     * If true, the tool does not need to be returned or tracked individually.
     */
    private Boolean consumable;

    /**
     * The minimal quantity required in stock before a notification or alert should be triggered.
     * This field is optional.
     */
    private Integer minimalRegistration;

    /**
     * The current status of the tool (true = active, false = deactivated).
     *
     * @NotNull - This field is required.
     */
    private Boolean status;

    /**
     * The name of the category to which this tool belongs.
     * If the category does not exist, it will be created automatically.
     *
     * @NotNull - This field is required.
     */
    @NotNull(message = "Category is required")
    private String category;
}
