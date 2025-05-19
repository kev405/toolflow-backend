package com.codeflow.toolflow.dto.loan;

import com.codeflow.toolflow.util.enums.LoanStatus;
import lombok.*;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing a request to create or update a loan.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoanRequest {

    /**
     * The ID of the teacher responsible for initiating the loan.
     * This field is required when the user is an administrator.
     */
    private Long teacherId;

    /**
     * The due date by which the tools must be returned.
     * Expected format: "YYYY-MM-DD".
     */
    private String dueDate;

    /**
     * The ID of the user primarily responsible for the loan (e.g., a student).
     */
    private Long responsibleId;

    /**
     * Optional notes or observations regarding the loan.
     * For example: usage context, additional instructions, or limitations.
     */
    private String notes;

    /**
     * The current status of the loan (e.g., ORDER, ON_LOAN, RETURNED, etc.).
     * This field can be used for updates; it's usually set by the system.
     */
    private LoanStatus loanStatus;

    /**
     * The list of tools included in the loan, along with requested quantities and other metadata.
     */
    private List<LoanToolRequest> tools;
}
