package com.codeflow.toolflow.persistence.loan.repository;

import com.codeflow.toolflow.persistence.loan.entity.Loan;
import com.codeflow.toolflow.util.enums.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long>, JpaSpecificationExecutor<Loan> {
    @Query("""
                SELECT l FROM Loan l
                WHERE (:teacherId IS NULL OR l.teacher.id = :teacherId)
                  AND (:responsibleId IS NULL OR l.responsible.id = :responsibleId)
                  AND (:loanStatus IS NULL OR l.loanStatus = :loanStatus)
            """)
    Page<Loan> findByFiltersWithoutDueDate(
            @Param("teacherId") Long teacherId,
            @Param("responsibleId") Long responsibleId,
            @Param("loanStatus") LoanStatus loanStatus,
            Pageable pageable
    );

    @Query("""
                SELECT l FROM Loan l
                WHERE (:teacherId IS NULL OR l.teacher.id = :teacherId)
                  AND (:responsibleId IS NULL OR l.responsible.id = :responsibleId)
                  AND l.dueDate = :dueDate
                  AND (:loanStatus IS NULL OR l.loanStatus = :loanStatus)
            """)
    Page<Loan> findByFiltersWithDueDate(
            @Param("teacherId") Long teacherId,
            @Param("responsibleId") Long responsibleId,
            @Param("dueDate") LocalDate dueDate,
            @Param("loanStatus") LoanStatus loanStatus,
            Pageable pageable
    );
}