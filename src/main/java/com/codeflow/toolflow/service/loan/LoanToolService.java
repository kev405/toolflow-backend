package com.codeflow.toolflow.service.loan;

import com.codeflow.toolflow.dto.loan.LoanToolRequest;
import com.codeflow.toolflow.persistence.loan.entity.Loan;

import java.util.List;

/**
 * Interface for handling tool operations associated with a loan.
 */
public interface LoanToolService {

    /**
     * Updates the list of tools associated with a given loan.
     * @param loan         the loan entity being updated
     * @param updatedTools the list of tool modifications requested
     * @param isAdmin      whether the current user has administrative privileges
     */
    void updateToolsForLoan(Loan loan, List<LoanToolRequest> updatedTools, boolean isAdmin, boolean isAllowPartialEdit);
}
