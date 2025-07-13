package com.codeflow.toolflow.controller.transfer;

import com.codeflow.toolflow.dto.ApiError;
import com.codeflow.toolflow.dto.transfer.TransferRequest;
import com.codeflow.toolflow.dto.transfer.TransferResponse;
import com.codeflow.toolflow.service.transfer.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing asset transfers between headquarters.
 */
@RestController
@RequestMapping("/api/transfers")
@Tag(name = "Transfers", description = "API for creating and managing asset transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @Operation(summary = "Get a paginated list of transfers with dynamic filters",
            description = "Retrieves transfers based on a combination of optional filters. Multi-select filters accept comma-separated values.")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    public ResponseEntity<Page<TransferResponse>> getAllTransfers(
            @Parameter(description = "Filter by origin headquarter ID") @RequestParam(required = false) Long originId,
            @Parameter(description = "Filter by destination headquarter ID") @RequestParam(required = false) Long destinationId,
            @Parameter(description = "Filter by a specific transfer date (format: YYYY-MM-DD)") @RequestParam(required = false) String transferDate,
            @Parameter(description = "Filter by tool IDs (comma-separated)") @RequestParam(required = false) List<Long> toolIds,
            @Parameter(description = "Filter by vehicle part IDs (comma-separated)") @RequestParam(required = false) List<Long> partIds,
            @Parameter(description = "Filter by vehicle IDs (comma-separated)") @RequestParam(required = false) List<Long> vehicleIds,
            Pageable pageable) {

        Page<TransferResponse> transfers = transferService.getAllTransfers(
                originId, destinationId, transferDate, toolIds, partIds, vehicleIds, pageable
        );
        return ResponseEntity.ok(transfers);
    }

    @Operation(summary = "Create a new transfer request",
            description = "Registers a new transfer with a 'PENDING' status. The request is validated for stock availability and business rules before creation.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Transfer request created successfully",
                    content = @Content(schema = @Schema(implementation = TransferResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or business rule violation",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    public ResponseEntity<TransferResponse> createTransfer(@Valid @RequestBody TransferRequest request) {
        TransferResponse response = transferService.createTransfer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update a pending transfer request",
            description = "Updates the details of an existing transfer, provided it is still in 'PENDING' status. All previous items are replaced with the new ones provided.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transfer updated successfully",
                    content = @Content(schema = @Schema(implementation = TransferResponse.class))),
            @ApiResponse(responseCode = "404", description = "Transfer not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Conflict: Transfer is not in 'PENDING' state",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    public ResponseEntity<TransferResponse> updateTransfer(
            @Parameter(description = "ID of the transfer to update") @PathVariable Long id,
            @Valid @RequestBody TransferRequest request) {
        TransferResponse response = transferService.updateTransfer(id, request);
        return ResponseEntity.ok(response);
    }

//    @Operation(summary = "Get a paginated list of transfers",
//            description = "Retrieves a list of all transfers, sorted and paginated.")
//    @GetMapping
//    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
//    public Page<TransferResponse> getAllTransfers(Pageable pageable) {
//        return transferService.getAllTransfers(pageable);
//    }

    @Operation(summary = "Get a transfer by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transfer found",
                    content = @Content(schema = @Schema(implementation = TransferResponse.class))),
            @ApiResponse(responseCode = "404", description = "Transfer not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    public ResponseEntity<TransferResponse> getTransferById(@Parameter(description = "ID of the transfer to retrieve") @PathVariable Long id) {
        return ResponseEntity.ok(transferService.getTransferById(id));
    }

    @Operation(summary = "Accept a pending transfer",
            description = "Changes the status of a 'PENDING' transfer to 'ACCEPTED', executing all inventory movements. This action is irreversible.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transfer accepted successfully",
                    content = @Content(schema = @Schema(implementation = TransferResponse.class))),
            @ApiResponse(responseCode = "404", description = "Transfer not found"),
            @ApiResponse(responseCode = "409", description = "Conflict: Transfer is not in 'PENDING' state",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}/accept")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    public ResponseEntity<TransferResponse> acceptTransfer(@Parameter(description = "ID of the transfer to accept") @PathVariable Long id) {
        TransferResponse response = transferService.acceptTransfer(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cancel a pending transfer",
            description = "Changes the status of a 'PENDING' transfer to 'CANCELLED'. No inventory is moved. This action is irreversible.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transfer cancelled successfully",
                    content = @Content(schema = @Schema(implementation = TransferResponse.class))),
            @ApiResponse(responseCode = "404", description = "Transfer not found"),
            @ApiResponse(responseCode = "409", description = "Conflict: Transfer is not in 'PENDING' state",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    public ResponseEntity<TransferResponse> cancelTransfer(@Parameter(description = "ID of the transfer to cancel") @PathVariable Long id) {
        TransferResponse response = transferService.cancelTransfer(id);
        return ResponseEntity.ok(response);
    }
}
