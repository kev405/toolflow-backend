package com.codeflow.toolflow.service.loan.impl;

import com.codeflow.toolflow.dto.auth.UserLogin;
import com.codeflow.toolflow.dto.loan.LoanRequest;
import com.codeflow.toolflow.dto.loan.LoanResponse;
import com.codeflow.toolflow.dto.tool.ToolStockRequest;
import com.codeflow.toolflow.mapper.loan.LoanMapper;
import com.codeflow.toolflow.persistence.loan.entity.Loan;
import com.codeflow.toolflow.persistence.loan.entity.LoanTool;
import com.codeflow.toolflow.persistence.loan.repository.LoanRepository;
import com.codeflow.toolflow.persistence.tool.entity.Tool;
import com.codeflow.toolflow.persistence.tool.entity.ToolInventory;
import com.codeflow.toolflow.persistence.tool.repository.ToolRepository;
import com.codeflow.toolflow.persistence.user.entity.User;
import com.codeflow.toolflow.persistence.user.repository.UserRepository;
import com.codeflow.toolflow.service.headquarter.impl.HeadquarterServiceImpl;
import com.codeflow.toolflow.service.loan.LoanService;
import com.codeflow.toolflow.service.tool.impl.ToolServiceImpl;
import com.codeflow.toolflow.util.enums.LoanStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final ToolRepository toolRepository;
    private final ToolServiceImpl toolService;
    private final LoanToolServiceImpl loanToolService;
    private final LoanMapper loanMapper;

    @Override
    @Transactional
    public LoanResponse registerOne(LoanRequest request) {
        Long currentUserId = getCurrentUserId();

        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found"));

        User responsible = userRepository.findById(request.getResponsibleId())
                .orElseThrow(() -> new EntityNotFoundException("Responsible not found"));

        Loan loan = loanMapper.toEntity(request);
        loan.setTeacher(teacher);
        loan.setResponsible(responsible);
        loan.setStatus(true);
        loan.setCreatedAt(LocalDateTime.now());
        loan.setCreatedBy(currentUserId);
        loan.setUpdatedAt(LocalDateTime.now());
        loan.setUpdatedBy(currentUserId);

        List<LoanTool> loanTools = request.getTools().stream().map(toolReq -> {
            Tool tool = toolRepository.findById(toolReq.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Tool not found: ID " + toolReq.getId()));

            int loaned = toolReq.getLoaned() != null ? toolReq.getLoaned() : 0;
            ToolInventory mainInventory = tool.getInventories().stream()
                    .filter(inv -> inv.getHeadquarter().getMain())
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException(
                            "No inventory in main headquarter for tool " + tool.getId()));

            int currentAvailable = Optional.ofNullable(mainInventory.getAvailable()).orElse(0);
            int currentOnLoan    = Optional.ofNullable(mainInventory.getOnLoan()).orElse(0);

            int newAvailable = currentAvailable - loaned;
            int newOnLoan = (tool.getConsumable() != null && tool.getConsumable())
                    ? currentOnLoan
                    : currentOnLoan + loaned;

            toolService.updateStockMain(tool.getId(), ToolStockRequest.builder()
                    .available(Math.max(newAvailable, 0))
                    .onLoan(newOnLoan)
                    .damaged(tool.getDamaged())
                    .build());

            User responsibleUnique = null;
            if (toolReq.getResponsibleId() != null) {
                responsibleUnique = userRepository.findById(toolReq.getResponsibleId()).orElse(null);
            }

            return LoanTool.builder()
                    .loan(loan)
                    .tool(tool)
                    .requested(toolReq.getRequested())
                    .loaned(loaned)
                    .delivered(toolReq.getDelivered() != null ? toolReq.getDelivered() : 0)
                    .damaged(toolReq.getDamaged() != null ? toolReq.getDamaged() : 0)
                    .notes(toolReq.getNotes())
                    .responsible(responsibleUnique)
                    .build();
        }).toList();

        boolean allConsumables = loanTools.stream()
                .allMatch(tool -> tool.getTool().getConsumable());

        LoanStatus initialStatus = Optional.ofNullable(loan.getLoanStatus()).orElse(LoanStatus.ORDER);

        if (allConsumables && initialStatus != LoanStatus.ORDER) {
            loan.setLoanStatus(LoanStatus.FINALIZED);
            loan.setReceivedDate(LocalDate.now());
        }

        loan.setLoanTools(loanTools);
        return loanMapper.toResponse(loanRepository.save(loan));
    }

    @Override
    public Page<LoanResponse> getAll(Pageable pageable, List<String> filters) {
        Long teacherId = null;
        Long responsibleId = null;
        LocalDate dueDate = null;
        LoanStatus loanStatus = null;
        List<Long> toolIds = null;

        List<String> auxFilters = Optional.ofNullable(filters).orElse(List.of());
        for (String filter : auxFilters) {
            String[] parts = filter.split(":", 2);
            if (parts.length != 2) continue;

            String key = parts[0];
            String value = parts[1];

            if (value.isBlank()) continue;

            switch (key) {
                case "teacherId" -> teacherId = Long.parseLong(value);
                case "responsibleId" -> responsibleId = Long.parseLong(value);
                case "dueDate" -> dueDate = LocalDate.parse(value.trim());
                case "loanStatus" -> loanStatus = LoanStatus.valueOf(value);
                case "toolIds" -> {
                    toolIds = Stream.of(value.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .map(Long::parseLong)
                            .toList();
                }
            }
        }

        List<String> roles = getCurrentUserRoles();
        Long currentUserId = getCurrentUserId();

        if (roles.size() == 1 && roles.contains("TEACHER")) {
            teacherId = currentUserId;
        }

        if (dueDate != null) {
            return loanRepository.findByFiltersWithDueDate(
                    teacherId, responsibleId, dueDate, loanStatus, toolIds, pageable
            ).map(loanMapper::toResponse);
        } else {
            return loanRepository.findByFiltersWithoutDueDate(
                    teacherId, responsibleId, loanStatus, toolIds, pageable
            ).map(loanMapper::toResponse);
        }
    }

    @Transactional
    @Override
    public LoanResponse updateOne(LoanRequest loanRequest, Long loanId) {
        Long currentUserId = getCurrentUserId();

        Loan existingLoan = loanRepository.findById(loanId)
                .orElseThrow(() -> new EntityNotFoundException("Loan not found with ID: " + loanId));

        LoanStatus currentStatus = existingLoan.getLoanStatus();

        boolean isFinalized = currentStatus == LoanStatus.FINALIZED || currentStatus == LoanStatus.DAMAGED_FINALIZED;
        boolean isAllowPartialEdit = currentStatus == LoanStatus.MISSING_FINALIZED || currentStatus == LoanStatus.MISSING_AND_DAMAGED_FINALIZED;
        boolean isAdmin = getCurrentUserRoles().contains("ADMINISTRATOR") || getCurrentUserRoles().contains("TOOL_ADMINISTRATOR");

        if (isFinalized) {
            throw new IllegalStateException("No se puede modificar un préstamo finalizado.");
        }

        if (!isAllowPartialEdit) {
            existingLoan.setResponsible(
                    loanRequest.getResponsibleId() != null
                            ? userRepository.findById(loanRequest.getResponsibleId())
                            .orElseThrow(() -> new EntityNotFoundException("Responsible not found"))
                            : null
            );
            existingLoan.setNotes(loanRequest.getNotes());
        }

        existingLoan.setNotes(loanRequest.getNotes());
        existingLoan.setUpdatedAt(LocalDateTime.now());
        existingLoan.setUpdatedBy(currentUserId);

        loanToolService.updateToolsForLoan(existingLoan, loanRequest.getTools(), isAdmin, isAllowPartialEdit);

        updateLoanStatus(existingLoan);

        Loan savedLoan = loanRepository.save(existingLoan);
        return loanMapper.toResponse(savedLoan);
    }

    @Transactional
    @Override
    public void deleteOne(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new EntityNotFoundException("Loan not found with ID: " + loanId));

        if (loan.getLoanStatus() != LoanStatus.ORDER) {
            throw new IllegalStateException("Only loans in ORDER status can be deleted.");
        }

        loan.setStatus(false);
        loan.setLoanStatus(LoanStatus.CANCELLED);
        loan.setUpdatedAt(LocalDateTime.now());
        loan.setUpdatedBy(getCurrentUserId());

        loanRepository.save(loan);
    }

    private Long getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserLogin userDetails) {
            return userDetails.getId();
        }
        throw new IllegalStateException("No authenticated user found.");
    }

    private List<String> getCurrentUserRoles() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities() != null) {
            return auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
        }
        return List.of();
    }

    private void updateLoanStatus(Loan loan) {
        boolean allConsumables = loan.getLoanTools().stream()
                .allMatch(tool -> tool.getTool().getConsumable());

        boolean allLoanedAssigned = loan.getLoanTools().stream()
                .allMatch(tool -> tool.getLoaned() != null && tool.getLoaned() > 0);

        boolean allDelivered = loan.getLoanTools().stream()
                .allMatch(tool ->
                        tool.getTool().getConsumable() ||
                                Optional.ofNullable(tool.getDelivered()).orElse(0) + Optional.ofNullable(tool.getDamaged()).orElse(0) > 0
                );


        boolean anyMissing = loan.getLoanTools().stream()
                .filter(tool -> !tool.getTool().getConsumable())
                .anyMatch(tool -> {
                    int loaned = Optional.ofNullable(tool.getLoaned()).orElse(0);
                    int delivered = Optional.ofNullable(tool.getDelivered()).orElse(0);
                    return delivered < loaned;
                });

        boolean anyDamaged = loan.getLoanTools().stream()
                .filter(tool -> !tool.getTool().getConsumable())
                .anyMatch(tool -> Optional.ofNullable(tool.getDamaged()).orElse(0) > 0);

        LoanStatus current = loan.getLoanStatus();

        if (allConsumables && allLoanedAssigned) {
            loan.setLoanStatus(LoanStatus.FINALIZED);
            loan.setReceivedDate(LocalDate.now());
            return;
        }

        if (current == LoanStatus.ORDER && allLoanedAssigned) {
            loan.setLoanStatus(LoanStatus.ON_LOAN);
            return;
        }

        if (current == LoanStatus.ON_LOAN || current == LoanStatus.OVERDUE) {
            if (allDelivered) {
                if (anyMissing && anyDamaged) {
                    loan.setLoanStatus(LoanStatus.MISSING_AND_DAMAGED_FINALIZED);
                } else if (anyMissing) {
                    loan.setLoanStatus(LoanStatus.MISSING_FINALIZED);
                } else if (anyDamaged) {
                    loan.setLoanStatus(LoanStatus.DAMAGED_FINALIZED);
                } else {
                    loan.setLoanStatus(LoanStatus.FINALIZED);
                }
                loan.setReceivedDate(LocalDate.now());
            }
            return;
        }

        if (current == LoanStatus.MISSING_FINALIZED || current == LoanStatus.MISSING_AND_DAMAGED_FINALIZED) {
            if (!anyMissing && !anyDamaged && allDelivered) {
                loan.setLoanStatus(LoanStatus.FINALIZED);
                loan.setReceivedDate(LocalDate.now());
            } else if (!anyMissing && anyDamaged && allDelivered) {
                loan.setLoanStatus(LoanStatus.DAMAGED_FINALIZED);
                loan.setReceivedDate(LocalDate.now());
            }
        }
    }

    @Override
    public List<Loan> getAllLoans() {
        return loanRepository.findAll()
                .stream()
                .toList();
    }

}
