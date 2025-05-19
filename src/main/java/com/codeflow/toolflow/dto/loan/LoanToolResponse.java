package com.codeflow.toolflow.dto.loan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing the details of a specific tool within a loan.
 * Contains information about requested, loaned, delivered, and damaged quantities,
 * as well as the person responsible for the tool.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanToolResponse {

    /**
     * The unique identifier of the tool instance within the loan.
     */
    private Long id;

    /**
     * The display name of the tool.
     */
    private String toolName;

    /**
     * The unique identifier of the tool itself (referencing the inventory).
     */
    private Long toolId;

    /**
     * The number of units of this tool requested in the loan.
     */
    private Integer requested;

    /**
     * The number of units actually loaned out.
     */
    private Integer loaned;

    /**
     * The number of units delivered back to the inventory.
     */
    private Integer delivered;

    /**
     * The number of units returned in a damaged condition.
     */
    private Integer damaged;

    /**
     * Additional notes or comments about the tool's usage or condition.
     */
    private String notes;

    /**
     * Summary information about the user responsible for this tool during the loan.
     */
    private LoanResponse.UserSummary responsible;
}
