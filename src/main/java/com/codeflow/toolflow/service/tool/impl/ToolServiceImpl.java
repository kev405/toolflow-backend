package com.codeflow.toolflow.service.tool.impl;

import com.codeflow.toolflow.dto.auth.UserLogin;
import com.codeflow.toolflow.dto.tool.ToolRequest;
import com.codeflow.toolflow.dto.tool.ToolResponse;
import com.codeflow.toolflow.dto.tool.ToolSimpleResponse;
import com.codeflow.toolflow.dto.tool.ToolStockRequest;
import com.codeflow.toolflow.mapper.tool.ToolMapper;
import com.codeflow.toolflow.persistence.loan.repository.LoanRepository;
import com.codeflow.toolflow.persistence.tool.entity.Tool;
import com.codeflow.toolflow.persistence.tool.entity.ToolInventory;
import com.codeflow.toolflow.persistence.tool.repository.ToolRepository;
import com.codeflow.toolflow.persistence.tool.repository.ToolSpecifications;
import com.codeflow.toolflow.service.category.CategoryService;
import com.codeflow.toolflow.service.email.EmailService;
import com.codeflow.toolflow.service.headquarter.HeadquarterService;
import com.codeflow.toolflow.service.tool.ToolService;
import com.codeflow.toolflow.util.exception.ToolNotFoundException;
import com.codeflow.toolflow.util.exception.ToolStillOnLoanException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for tool-related operations.
 * <p>
 * This service handles the core business logic for creating, updating, retrieving,
 * deleting, and listing tools, as well as category resolution and audit information tracking.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class ToolServiceImpl implements ToolService {

    private final ToolRepository toolRepository;
    private final CategoryService categoryService;
    private final HeadquarterService headquarterService;
    private final LoanRepository loanRepository;             // 👈 nuevo
    private final ToolMapper toolMapper;

    @Value("${email.admin.from}")
    private String adminEmail;

    private EmailService emailService;

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Registers a new tool in the system based on the data received in the {@link ToolRequest}.
     * Automatically creates a category if it does not exist, and tracks audit information
     * for the creator.
     *
     * @param request the request object containing tool data to be saved
     * @return the created {@link ToolResponse}
     */
    @Override
    public ToolResponse registerOneTool(ToolRequest request) {
        Tool tool = mapCreateRequestToEntity(request);
        tool.setStatus(true);

        tool.setAvailable(Optional.ofNullable(tool.getAvailable()).orElse(0));
        tool.setOnLoan(0);
        tool.setDamaged(0);
        tool.setQuantity(tool.getAvailable());

        ToolInventory inventory = ToolInventory.builder()
                .tool(tool)
                .headquarter(headquarterService.getMainHeadquarter())
                .quantity(tool.getQuantity())
                .available(tool.getAvailable())
                .damaged(tool.getDamaged())
                .onLoan(tool.getOnLoan())
                .createdAt(LocalDateTime.now())
                .createdBy(getCurrentUserId())
                .updatedAt(LocalDateTime.now())
                .updatedBy(getCurrentUserId())
                .build();

        tool.getInventories().add(inventory);

        Tool saved = toolRepository.save(tool);

        return toolMapper.toResponse(saved);
    }

    /**
     * Updates the details of an existing tool identified by its ID.
     * The method retrieves the existing record, applies updates from the {@link ToolRequest},
     * and tracks audit metadata such as last updater and timestamp.
     *
     * @param id      the ID of the tool to be updated
     * @param request the object containing the updated tool data
     * @return the updated {@link ToolResponse}
     */
    @Override
    public ToolResponse updateOneTool(Long id, ToolRequest request) {
        Tool existing = findOneById(id);
        Integer previousAvailable = existing.getAvailable();

        Tool updated = mapUpdateRequestToEntity(request, existing);

        Tool saved = toolRepository.save(updated);

        checkAndNotifyLowStock(saved, previousAvailable);

        return toolMapper.toResponse(saved);
    }

    /**
     * Retrieves a tool by its ID and returns its full details.
     *
     * @param id the unique identifier of the tool
     * @return the {@link ToolResponse} containing tool details
     * @throws ToolNotFoundException if the tool is not found
     */
    @Override
    public ToolResponse getOne(Long id) {
        return toolMapper.toResponse(findOneById(id));
    }

    /**
     * Performs a soft delete on a tool by setting its status to false.
     * This method does not remove the tool from the database.
     *
     * @param id the ID of the tool to deactivate
     */
    @Override
    public void deleteOneTool(Long id) {
        Tool tool = findOneById(id);

        if (loanRepository.existsActiveLoanByTool(id)) {
            throw new ToolStillOnLoanException(
                    "No se puede desactivar la herramienta; está asociada a préstamos en curso."
            );
        }

        tool.setStatus(false);
        tool.setUpdatedAt(LocalDateTime.now());
        tool.setUpdatedBy(getCurrentUserId());
        toolRepository.save(tool);
    }

    /**
     * Retrieves a paginated list of active tools, optionally filtered by a search query
     * and search column. The results are dynamically built using specifications.
     *
     * @param pageable the pagination and sorting information
     * @param filters  a list of search filters in the format {@code column:value1,value2,...}
     * @return a {@link Page} of {@link ToolResponse} objects matching the filters
     */
    @Override
    public Page<ToolResponse> getPage(Pageable pageable, List<String> filters) {
        Specification<Tool> spec = Specification.where(ToolSpecifications.toolIsActive());

        if (filters != null && !filters.isEmpty()) {
            for (String filter : filters) {
                String[] parts = filter.split(":");
                if (parts.length >= 2) {
                    String column = parts[0].trim();
                    String[] values = parts[1].split(",");
                    spec = spec.and(ToolSpecifications.searchByColumnValues(column, Arrays.asList(values)));
                }
            }
        }

        Page<Tool> tools = toolRepository.findAll(spec, pageable);
        return tools.map(toolMapper::toResponse);
    }

    /**
     * Updates the stock levels of a tool in the main headquarter.
     * This method adjusts the available, damaged, and on-loan quantities
     * in the tool's inventory for the main headquarter and checks for low stock conditions.
     *
     * @param id      the ID of the tool to update
     * @param request the request object containing stock updates
     * @return the updated {@link ToolResponse}
     */
    @Override
    public ToolResponse updateStockMain(Long id, ToolStockRequest request) {
        Tool tool = toolRepository.findById(id)
                .orElseThrow(() -> new ToolNotFoundException("Tool " + id + " not found"));

        int previousAvailable = tool.getInventories().stream()
                .mapToInt(inv -> Optional.ofNullable(inv.getAvailable()).orElse(0))
                .sum();

        ToolInventory inventory = tool.getInventories().stream()
                .filter(inv -> inv.getHeadquarter().equals(headquarterService.getMainHeadquarter()))
                .findFirst()
                .orElseThrow(() -> new ToolNotFoundException("No inventory in main headquarter"));

        if (request.getAvailable() != null) inventory.setAvailable(request.getAvailable());
        if (request.getDamaged() != null) inventory.setDamaged(request.getDamaged());
        if (request.getOnLoan() != null) inventory.setOnLoan(request.getOnLoan());

        inventory.setQuantity(
                inventory.getAvailable() + inventory.getDamaged() + inventory.getOnLoan()
        );

        inventory.setUpdatedAt(LocalDateTime.now());
        inventory.setUpdatedBy(getCurrentUserId());

        int totalAvailable = tool.getInventories().stream()
                .mapToInt(inv -> Optional.ofNullable(inv.getAvailable()).orElse(0))
                .sum();

        int totalOnLoan = tool.getInventories().stream()
                .mapToInt(inv -> Optional.ofNullable(inv.getOnLoan()).orElse(0))
                .sum();

        int totalDamaged = tool.getInventories().stream()
                .mapToInt(inv -> Optional.ofNullable(inv.getDamaged()).orElse(0))
                .sum();

        tool.setAvailable(totalAvailable);
        tool.setOnLoan(totalOnLoan);
        tool.setDamaged(totalDamaged);
        tool.setQuantity(totalAvailable + totalOnLoan + totalDamaged);

        checkAndNotifyLowStock(tool, previousAvailable);

        Tool savedTool = toolRepository.save(tool);

        return toolMapper.toResponse(savedTool);
    }

    /**
     * Updates the stock levels of a tool in a specific headquarter.
     * This method adjusts the available, damaged, and on-loan quantities
     * in the tool's inventory for the specified headquarter and checks for low stock conditions.
     *
     * @param toolId        the ID of the tool to update
     * @param headquarterId the ID of the headquarter where the stock is being updated
     * @param request       the request object containing stock updates
     * @return the updated {@link ToolResponse}
     */
    @Override
    public ToolResponse updateStockByHeadquarter(Long toolId, Long headquarterId, ToolStockRequest request) {
        Tool tool = toolRepository.findById(toolId)
                .orElseThrow(() -> new ToolNotFoundException("Tool " + toolId + " not found"));

        int previousAvailable = tool.getInventories().stream()
                .mapToInt(inv -> Optional.ofNullable(inv.getAvailable()).orElse(0))
                .sum();

        ToolInventory inventory = tool.getInventories().stream()
                .filter(inv -> inv.getHeadquarter().getId().equals(headquarterId))
                .findFirst()
                .orElseThrow(() -> new ToolNotFoundException("No inventory in headquarter ID " + headquarterId));

        if (request.getAvailable() != null) inventory.setAvailable(request.getAvailable());
        if (request.getDamaged() != null) inventory.setDamaged(request.getDamaged());
        if (request.getOnLoan() != null) inventory.setOnLoan(request.getOnLoan());

        inventory.setQuantity(
                inventory.getAvailable() + inventory.getDamaged() + inventory.getOnLoan()
        );

        inventory.setUpdatedAt(LocalDateTime.now());
        inventory.setUpdatedBy(getCurrentUserId());

        int totalAvailable = tool.getInventories().stream()
                .mapToInt(inv -> Optional.ofNullable(inv.getAvailable()).orElse(0))
                .sum();

        int totalOnLoan = tool.getInventories().stream()
                .mapToInt(inv -> Optional.ofNullable(inv.getOnLoan()).orElse(0))
                .sum();

        int totalDamaged = tool.getInventories().stream()
                .mapToInt(inv -> Optional.ofNullable(inv.getDamaged()).orElse(0))
                .sum();

        tool.setAvailable(totalAvailable);
        tool.setOnLoan(totalOnLoan);
        tool.setDamaged(totalDamaged);
        tool.setQuantity(totalAvailable + totalOnLoan + totalDamaged);

        checkAndNotifyLowStock(tool, previousAvailable);

        Tool savedTool = toolRepository.save(tool);
        return toolMapper.toResponse(savedTool);
    }

    /**
     * Retrieves all tools in the system.
     *
     * @return a list of {@link ToolResponse} objects representing each tool
     */
    @Override
    public List<ToolResponse> getAll() {
        return toolRepository.findAll()
                .stream()
                .map(toolMapper::toResponse)
                .toList();
    }

    /**
     * Retrieves a {@link Tool} entity by its ID.
     *
     * @param id the ID of the tool
     * @return the {@link Tool} entity
     * @throws ToolNotFoundException if the tool does not exist
     */
    private Tool findOneById(Long id) {
        return toolRepository.findById(id).orElseThrow(() -> new ToolNotFoundException("Tool not found"));
    }

    /**
     * Maps a {@link ToolRequest} DTO into a new {@link Tool} entity.
     * This method is used specifically for creating new tools, ensuring
     * that all necessary fields are initialized correctly.
     *
     * @param request the request object with input data
     * @return the mapped {@link Tool} entity ready to be persisted
     */
    private Tool mapCreateRequestToEntity(ToolRequest request) {
        Long userId = getCurrentUserId();

        Tool tool = toolMapper.toNewEntity(request);
        tool.setCreatedAt(LocalDateTime.now());
        tool.setCreatedBy(userId);
        tool.setUpdatedAt(LocalDateTime.now());
        tool.setUpdatedBy(userId);
        tool.setStatus(true);

        tool.setCategory(categoryService.findOrCreateByName(request.getCategory()));
        tool.setInventories(new ArrayList<>());

        return tool;
    }


    /**
     * Maps a {@link ToolRequest} DTO into an existing {@link Tool} entity.
     * This method is used specifically for updating existing tools, ensuring
     * that all necessary fields are preserved and updated correctly.
     *
     * @param request      the request object with input data
     * @param existingTool the existing tool entity to be updated
     * @return the mapped {@link Tool} entity ready to be persisted
     */
    private Tool mapUpdateRequestToEntity(ToolRequest request, Tool existingTool) {
        Tool tool = toolMapper.toExistingEntity(request);
        tool.setId(existingTool.getId());

        Long userId = getCurrentUserId();
        tool.setUpdatedAt(LocalDateTime.now());
        tool.setUpdatedBy(userId);

        tool.setCategory(categoryService.findOrCreateByName(request.getCategory()));

        tool.setCreatedAt(existingTool.getCreatedAt());
        tool.setCreatedBy(existingTool.getCreatedBy());
        tool.setStatus(existingTool.getStatus());
        tool.setInventories(existingTool.getInventories());
        tool.setQuantity(existingTool.getQuantity());
        tool.setAvailable(existingTool.getAvailable());
        tool.setOnLoan(existingTool.getOnLoan());
        tool.setDamaged(existingTool.getDamaged());

        return tool;
    }

    /**
     * Retrieves the current authenticated user's ID from the security context.
     *
     * @return the user ID
     * @throws IllegalStateException if no authenticated user is found in the context
     */
    Long getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserLogin userDetails) {
            return userDetails.getId();
        }
        throw new IllegalStateException("No authenticated user found.");
    }

    /**
     * Checks if the tool's total available stock (across all headquarters)
     * has dropped below the minimal threshold. If so, sends a warning email.
     *
     * @param tool              the tool being evaluated
     * @param previousAvailable the total available before the update
     */
    private void checkAndNotifyLowStock(Tool tool, Integer previousAvailable) {
        Integer minimal = tool.getMinimalRegistration();
        Integer currentAvailable = tool.getAvailable();

        if (Boolean.TRUE.equals(tool.getConsumable()) &&
                previousAvailable != null &&
                minimal != null &&
                previousAvailable > minimal &&
                currentAvailable != null &&
                currentAvailable < minimal
        ) {
            String subject = "⚠️ Alerta: Stock mínimo alcanzado - " + tool.getToolName();
            String htmlBody = buildLowStockHtmlBody(tool);
            emailService.sendHtmlEmail(adminEmail, subject, htmlBody);
        }
    }

    /**
     * Retrieves a list of tools available in a specific headquarter.
     * This method filters tools based on their active status and checks
     * for inventory in the specified headquarter.
     *
     * @param headquarterId the ID of the headquarter to filter tools by
     * @return a list of {@link ToolSimpleResponse} objects representing available tools
     */
    @Override
    public List<ToolSimpleResponse> getToolsByHeadquarter(Long headquarterId) {
        List<Tool> tools = toolRepository.findAll(Specification.where(ToolSpecifications.toolIsActive()));

        List<ToolSimpleResponse> result = new ArrayList<>();

        for (Tool tool : tools) {
            Optional<ToolInventory> inventoryOpt = tool.getInventories().stream()
                    .filter(inv -> inv.getHeadquarter().getId().equals(headquarterId))
                    .findFirst();

            if (inventoryOpt.isPresent()) {
                ToolInventory inventory = inventoryOpt.get();

                ToolSimpleResponse dto = toolMapper.toSimpleResponse(tool, inventory);
                result.add(dto);
            }
        }

        return result;
    }


    private String buildLowStockHtmlBody(Tool tool) {

        String html = "<html><body style='font-family:Arial,sans-serif;'>" +
                "<h2 style='color:#d9534f;'>⚠️ Alerta de stock mínimo</h2>" +
                "<p>La herramienta <strong>\"" + tool.getToolName() +
                "\"</strong> ha alcanzado un <strong>nivel crítico de stock</strong>.</p>" +
                "<table border='1' cellpadding='8' cellspacing='0' style='border-collapse:collapse;margin-top:10px;'>" +
                "<thead><tr style='background-color:#f2f2f2;'>" +
                "<th>Herramienta</th><th>Disponible</th><th>Stock mínimo</th>" +
                "</tr></thead>" +
                "<tbody><tr>" +
                "<td>" + tool.getToolName() + "</td>" +
                "<td>" + tool.getAvailable() + "</td>" +
                "<td>" + tool.getMinimalRegistration() + "</td>" +
                "</tr></tbody></table>" +
                "<p style='margin-top:20px;'>📦 Te recomendamos considerar la reposición de esta herramienta lo antes posible para evitar inconvenientes operativos.</p>" +
                "<br><p style='font-size:small;color:gray;'>Este es un mensaje automático de ToolFlow. No responder directamente a este correo.</p>" +
                "</body></html>";

        return html;
    }
}
