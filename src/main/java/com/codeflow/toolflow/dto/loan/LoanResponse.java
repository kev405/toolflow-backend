package com.codeflow.toolflow.dto.loan;

import com.codeflow.toolflow.util.enums.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing the response data for a loan.
 * Contains summary information about the loan, including involved users and tools.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanResponse {

    /**
     * The unique identifier of the loan.
     */
    private Long id;

    /**
     * Summary information about the teacher who initiated the loan.
     */
    private UserSummary teacher;

    /**
     * Summary information about the primary responsible person for the loan.
     */
    private UserSummary responsible;

    /**
     * The due date by which the tools must be returned.
     * Represented in ISO-8601 format (e.g., "2025-05-20").
     */
    private String dueDate;

    /**
     * The headquarter where the loan is managed.
     * Contains details like name and address.
     */
    private String receivedDate;

    /**
     * Additional notes or comments related to the loan.
     */
    private String notes;

    /**
     * The current status of the loan, such as ORDER, ON_LOAN, or RETURNED.
     */
    private LoanStatus loanStatus;

    /**
     * The list of tools involved in the loan, each with specific details.
     */
    private List<LoanToolResponse> tools;

    /**
     * Indicates whether the loan is active (true) or has been logically deleted/inactive (false).
     */
    private boolean status;

    /**
     * Summary representation of a user, including only the ID and full name.
     * Used for both teacher and responsible fields to reduce payload size.
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserSummary {
        /**
         * The unique identifier of the user.
         */
        private Long id;

        /**
         * The full name of the user (e.g., "John Doe").
         */
        private String fullName;
    }
}
