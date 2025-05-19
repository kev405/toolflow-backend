package com.codeflow.toolflow.service.loan;

import com.codeflow.toolflow.dto.loan.LoanRequest;
import com.codeflow.toolflow.dto.loan.LoanResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interface defining business operations related to loan management.
 */
public interface LoanService {

    /**
     * Registers a new loan in the system.
     *
     * @param request the loan request payload containing tool, user, and date details
     * @return a {@link LoanResponse} representing the newly registered loan
     */
    LoanResponse registerOne(LoanRequest request);

    /**
     * Retrieves a paginated list of loans filtered by optional criteria such as teacher,
     * responsible user, due date, or status.
     *
     * @param pageable pagination and sorting configuration
     * @param filters  list of filters in the format {@code field:value}
     * @return a {@link Page} of {@link LoanResponse} objects matching the filter criteria
     */
    Page<LoanResponse> getAll(Pageable pageable, List<String> filters);

    /**
     * Updates the data of an existing loan, including the responsible user and associated tools.
     *
     * @param loanRequest updated loan data
     * @param loanId      the unique ID of the loan to update
     * @return the updated {@link LoanResponse}
     */
    LoanResponse updateOne(LoanRequest loanRequest, Long loanId);

    /**
     * Performs a logical deletion (soft delete) of a loan.
     *
     * @param loanId the ID of the loan to delete
     */
    void deleteOne(Long loanId);

    /**
     * Retrieves a list of all loans in the system.
     *
     * @return a list of {@link LoanResponse} objects representing all loans
     */
    List<LoanResponse> getAllLoans();
}
