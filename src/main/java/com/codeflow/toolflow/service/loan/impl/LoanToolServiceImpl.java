package com.codeflow.toolflow.service.loan.impl;

import com.codeflow.toolflow.dto.loan.LoanToolRequest;
import com.codeflow.toolflow.dto.tool.ToolStockRequest;
import com.codeflow.toolflow.persistence.loan.entity.Loan;
import com.codeflow.toolflow.persistence.loan.entity.LoanTool;
import com.codeflow.toolflow.persistence.tool.entity.Tool;
import com.codeflow.toolflow.persistence.tool.repository.ToolRepository;
import com.codeflow.toolflow.persistence.user.entity.User;
import com.codeflow.toolflow.persistence.user.repository.UserRepository;
import com.codeflow.toolflow.service.loan.LoanToolService;
import com.codeflow.toolflow.service.tool.impl.ToolServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanToolServiceImpl implements LoanToolService {

    private final ToolRepository toolRepository;
    private final UserRepository userRepository;
    private final ToolServiceImpl toolService;

    @Override
    public void updateToolsForLoan(Loan loan, List<LoanToolRequest> updatedTools, boolean isAdmin, boolean isAllowPartialEdit) {
        Map<Long, LoanToolRequest> incomingMap = updatedTools.stream()
                .collect(Collectors.toMap(LoanToolRequest::getId, Function.identity()));

        List<LoanTool> currentTools = getLoanTools(loan, isAdmin, incomingMap);

        for (LoanToolRequest toolReq : updatedTools) {
            Tool toolEntity = toolRepository.findById(toolReq.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Tool not found: ID " + toolReq.getId()));

            LoanTool existing = currentTools.stream()
                    .filter(t -> t.getTool().getId().equals(toolReq.getId()))
                    .findFirst()
                    .orElse(null);

            User responsible = toolReq.getResponsibleId() != null
                    ? userRepository.findById(toolReq.getResponsibleId()).orElse(null)
                    : null;

            if (existing != null) {
                int oldLoaned = existing.getLoaned();
                int newLoaned = isAdmin ? Optional.ofNullable(toolReq.getLoaned()).orElse(oldLoaned) : oldLoaned;

                int oldDelivered = existing.getDelivered();
                int newDelivered = isAdmin ? Optional.ofNullable(toolReq.getDelivered()).orElse(oldDelivered) : oldDelivered;

                int oldDamaged = existing.getDamaged();
                int newDamaged = isAdmin ? Optional.ofNullable(toolReq.getDamaged()).orElse(oldDamaged) : oldDamaged;

                if (isAdmin && !toolEntity.getConsumable()) {
                    boolean changed = false;
                    int updatedAvailable = toolEntity.getAvailable();
                    int updatedOnLoan = toolEntity.getOnLoan();
                    int updatedDamaged = toolEntity.getDamaged();

                    if (newLoaned != oldLoaned) {
                        int deltaLoaned = newLoaned - oldLoaned;
                        updatedAvailable -= deltaLoaned;
                        updatedOnLoan += deltaLoaned;
                        changed = true;
                    }

                    if (newDelivered != oldDelivered) {
                        int deltaDelivered = newDelivered - oldDelivered;
                        updatedAvailable += deltaDelivered;
                        updatedOnLoan -= deltaDelivered;
                        changed = true;
                    }

                    if (newDamaged != oldDamaged) {
                        int deltaDamaged = newDamaged - oldDamaged;
                        updatedAvailable -= deltaDamaged;
                        updatedDamaged += deltaDamaged;
                        changed = true;
                    }

                    if (changed) {
                        toolService.updateStock(toolEntity.getId(), ToolStockRequest.builder()
                                .available(Math.max(updatedAvailable, 0))
                                .onLoan(Math.max(updatedOnLoan, 0))
                                .damaged(Math.max(updatedDamaged, 0))
                                .build());
                    }
                }

                existing.setRequested(toolReq.getRequested());
                existing.setNotes(toolReq.getNotes());

                existing.setResponsible(responsible);

                if (isAdmin) {
                    existing.setLoaned(newLoaned);
                    existing.setDelivered(newDelivered);
                    existing.setDamaged(newDamaged);
                }
            } else {
                int loaned = isAdmin ? Optional.ofNullable(toolReq.getLoaned()).orElse(0) : 0;
                int delivered = isAdmin ? Optional.ofNullable(toolReq.getDelivered()).orElse(0) : 0;
                int damaged = isAdmin ? Optional.ofNullable(toolReq.getDamaged()).orElse(0) : 0;

                if (isAdmin && !toolEntity.getConsumable()) {
                    int returnedOk = delivered - damaged;
                    int updatedAvailable = toolEntity.getAvailable() - loaned + returnedOk;
                    int updatedOnLoan = toolEntity.getOnLoan() + loaned - delivered;
                    int updatedDamaged = toolEntity.getDamaged() + damaged;

                    toolService.updateStock(toolEntity.getId(), ToolStockRequest.builder()
                            .available(updatedAvailable)
                            .onLoan(updatedOnLoan)
                            .damaged(updatedDamaged)
                            .build());
                }

                LoanTool newTool = LoanTool.builder()
                        .loan(loan)
                        .tool(toolEntity)
                        .requested(toolReq.getRequested())
                        .loaned(loaned)
                        .delivered(isAdmin ? Optional.ofNullable(toolReq.getDelivered()).orElse(0) : 0)
                        .damaged(isAdmin ? Optional.ofNullable(toolReq.getDamaged()).orElse(0) : 0)
                        .notes(toolReq.getNotes())
                        .responsible(responsible)
                        .build();

                currentTools.add(newTool);
            }
        }
    }

    private List<LoanTool> getLoanTools(Loan loan, boolean isAdmin, Map<Long, LoanToolRequest> incomingMap) {
        List<LoanTool> currentTools = loan.getLoanTools();

        currentTools.removeIf(existingTool -> {
            boolean toRemove = !incomingMap.containsKey(existingTool.getTool().getId());
            if (toRemove) {
                Tool tool = existingTool.getTool();

                if (isAdmin && !tool.getConsumable()) {
                    int loaned = existingTool.getLoaned();
                    int delivered = existingTool.getDelivered();
                    int damaged = existingTool.getDamaged();

                    int returnedOk = delivered - damaged;
                    int updatedAvailable = tool.getAvailable() + Math.max(returnedOk, 0);
                    int updatedOnLoan = tool.getOnLoan() - loaned;
                    int updatedDamaged = tool.getDamaged() - damaged;

                    toolService.updateStock(tool.getId(), ToolStockRequest.builder()
                            .available(Math.max(updatedAvailable, 0))
                            .onLoan(Math.max(updatedOnLoan, 0))
                            .damaged(Math.max(updatedDamaged, 0))
                            .build());
                }

                existingTool.setLoan(null);
            }
            return toRemove;
        });

        return currentTools;
    }
}
