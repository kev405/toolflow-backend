package com.codeflow.toolflow.dto.loan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing the data for a single tool involved in a loan request.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanToolRequest {

    /**
     * The unique identifier of the tool being requested.
     * Must correspond to an existing tool in the inventory.
     */
    private Long id;

    /**
     * The number of units requested for loan.
     * This value must be greater than or equal to 0.
     */
    private Integer requested;

    /**
     * The number of units actually loaned to the user.
     * This value is usually set by an administrator or tool manager.
     */
    private Integer loaned;

    /**
     * The number of units returned by the user.
     * This value is used to determine whether the tool was returned fully, partially, or not at all.
     */
    private Integer delivered;

    /**
     * The number of units returned in damaged condition.
     * Helps track tool condition and decide on replacements or maintenance.
     */
    private Integer damaged;

    /**
     * Optional notes about the specific tool within the context of the loan.
     * For example: issues, usage notes, or packaging instructions.
     */
    private String notes;

    /**
     * The ID of the user responsible for this specific tool (different from the main responsible).
     * Useful when distributing responsibilities across multiple users.
     */
    private Long responsibleId;
}
