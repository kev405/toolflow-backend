package com.codeflow.toolflow.controller.vehiclepart;

import com.codeflow.toolflow.dto.ApiError;
import com.codeflow.toolflow.dto.vehiclepart.*;
import com.codeflow.toolflow.service.vehiclepart.VehiclePartService;
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

@RestController
@RequestMapping("/api/vehicle-parts")
@Tag(name = "Vehicle Parts", description = "API for managing vehicle parts and their inventory")
@RequiredArgsConstructor
public class VehiclePartController {

    private final VehiclePartService vehiclePartService;

    @Operation(summary = "Create a new vehicle part and its initial stock",
            description = "Registers a new part and creates its first inventory record in one transaction.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Part created successfully",
                    content = @Content(schema = @Schema(implementation = VehiclePartResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    public ResponseEntity<VehiclePartResponse> createPart(@Valid @RequestBody VehiclePartRequest request) {
        VehiclePartResponse response = vehiclePartService.createVehiclePartAndInventory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get a paginated list of vehicle parts",
            description = "Retrieves vehicle parts with optional filters for name, vehicle, and headquarter.")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    public Page<VehiclePartResponse> getParts(
            @Parameter(description = "Filter by part name") @RequestParam(required = false) String name,
            @Parameter(description = "Filter by associated vehicle ID") @RequestParam(required = false) Long vehicleId,
            @Parameter(description = "Filter by headquarter ID") @RequestParam(required = false) Long headquarterId,
            Pageable pageable) {
        return vehiclePartService.getPage(name, vehicleId, headquarterId, pageable);
    }

    @Operation(summary = "Get a vehicle part by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Part found",
                    content = @Content(schema = @Schema(implementation = VehiclePartResponse.class))),
            @ApiResponse(responseCode = "404", description = "Part not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    public ResponseEntity<VehiclePartResponse> getPartById(@PathVariable Long id) {
        return ResponseEntity.ok(vehiclePartService.getVehiclePartById(id));
    }

    @Operation(summary = "Update a vehicle part's details",
            description = "Updates core information of a part. Does not affect stock.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Part updated successfully",
                    content = @Content(schema = @Schema(implementation = VehiclePartResponse.class))),
            @ApiResponse(responseCode = "404", description = "Part not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    public ResponseEntity<VehiclePartResponse> updatePart(@PathVariable Long id, @Valid @RequestBody
    VehiclePartUpdateRequest request) {
        return ResponseEntity.ok(vehiclePartService.updateVehiclePart(id, request));
    }

    @Operation(summary = "Delete a vehicle part",
            description = "Performs a hard delete of a vehicle part.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Part deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Part not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    public ResponseEntity<Void> deletePart(@PathVariable Long id) {
        vehiclePartService.deleteVehiclePart(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Associate or disassociate inventory with a vehicle",
            description = "Updates an inventory record to link it to a specific vehicle or remove the link.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Association updated successfully"),
            @ApiResponse(responseCode = "404", description = "Part, inventory, or vehicle not found"),
            @ApiResponse(responseCode = "400", description = "Business rule violation (e.g., headquarters mismatch)")
    })
    @PutMapping("/{partId}/headquarters/{headquarterId}/association")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    public ResponseEntity<Void> associateVehicleWithPart(
            @Parameter(description = "ID of the vehicle part") @PathVariable Long partId,
            @Parameter(description = "ID of the headquarter where inventory is located") @PathVariable Long headquarterId,
            @Valid @RequestBody AssociateVehicleRequest request) {
        vehiclePartService.associateVehicle(partId, headquarterId, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update inventory stock for a part",
            description = "Sets the quantity for a specific part in a specific headquarter.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Stock updated successfully"),
            @ApiResponse(responseCode = "404", description = "Inventory record not found")
    })
    @PutMapping("/{partId}/headquarters/{headquarterId}/stock")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    public ResponseEntity<Void> updatePartStock(
            @Parameter(description = "ID of the vehicle part") @PathVariable Long partId,
            @Parameter(description = "ID of the headquarter") @PathVariable Long headquarterId,
            @Valid @RequestBody UpdateStockRequest request) {
        vehiclePartService.updateStock(partId, headquarterId, request);
        return ResponseEntity.noContent().build();
    }
}
