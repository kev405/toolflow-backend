package com.codeflow.toolflow.dto.tool;

import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * Data Transfer Object (DTO) used for optionally updating stock-related fields of a tool.
 * <p>
 * All fields in this DTO are optional; only non-null values will be updated.
 */
@Data
public class ToolStockRequest {

    /**
     * The number of units currently available for use.
     * This field is optional.
     *
     * @Min(value = 0) - Must be zero or positive if provided.
     */
    @Min(value = 0, message = "Available cannot be negative")
    private Integer available = 0;

    /**
     * The number of units currently marked as damaged or broken.
     * This field is optional.
     *
     * @Min(value = 0) - Must be zero or positive if provided.
     */
    @Min(value = 0, message = "Damaged cannot be negative")
    private Integer damaged = 0;

    /**
     * The number of units currently on loan (borrowed).
     * This field is optional.
     *
     * @Min(value = 0) - Must be zero or positive if provided.
     */
    @Min(value = 0, message = "On loan cannot be negative")
    private Integer onLoan = 0;
}
