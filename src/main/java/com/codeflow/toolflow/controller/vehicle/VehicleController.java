package com.codeflow.toolflow.controller.vehicle;

import com.codeflow.toolflow.dto.ApiError;
import com.codeflow.toolflow.dto.vehicle.TransferableVehicleResponse;
import com.codeflow.toolflow.dto.vehicle.VehicleRequest;
import com.codeflow.toolflow.dto.vehicle.VehicleResponse;
import com.codeflow.toolflow.service.vehicle.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller that exposes CRUD operations for {@link VehicleResponse} resources.
 * <p>
 * <strong>Security:</strong> Todas las rutas requieren el rol <code>ADMINISTRATOR</code>.
 * </p>
 */
@RestController
@RequestMapping("/vehicle")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @Operation(summary = "Get available vehicles for transfer",
            description = "Returns a list of all vehicles currently assigned to a specific origin headquarter, making them available for a transfer operation.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of available vehicles retrieved successfully.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = TransferableVehicleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - The required 'headquarterId' parameter is missing.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication token is missing or invalid."),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have the 'ADMINISTRATOR' role."),
            @ApiResponse(responseCode = "404", description = "Not Found - The specified 'headquarterId' does not exist.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/vehicles/available-for-transfer") // URL específica para claridad
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    public ResponseEntity<List<TransferableVehicleResponse>> getAvailableVehicles(
            @Parameter(description = "ID of the origin headquarter to list vehicles from.", required = true)
            @RequestParam Long headquarterId) {
        return ResponseEntity.ok(vehicleService.getAvailableVehicles(headquarterId));
    }


    @GetMapping(path = "/findBy")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    @Operation(
            summary = "Search vehicles",
            description = "Returns a paginated list of vehicles filtered by any combination of query parameters."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Page of vehicles retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = VehicleResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid filter or pagination parameters",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public Page<VehicleResponse> findBy(
            @Parameter(in = ParameterIn.QUERY, description = "Vehicle type, e.g. <em>Car</em>, <em>Truck</em>") @RequestParam(required = false) String vehicleType,
            @Parameter(in = ParameterIn.QUERY, description = "License-plate number") @RequestParam(required = false) String plate,
            @Parameter(in = ParameterIn.QUERY, description = "Model designation") @RequestParam(required = false) String model,
            @Parameter(in = ParameterIn.QUERY, description = "Primary exterior color") @RequestParam(required = false) String color,
            @Parameter(in = ParameterIn.QUERY, description = "Chassis / VIN number") @RequestParam(required = false) String numberChasis,
            @Parameter(in = ParameterIn.QUERY, description = "Manufacturer brand") @RequestParam(required = false) String brand,
            @Parameter(in = ParameterIn.QUERY, description = "Current location label or coordinates") @RequestParam(required = false) String location,
            @Parameter(in = ParameterIn.QUERY, description = "ID of the headquarter where the vehicle is registered") @RequestParam(required = false) Long headquarterId,
            Pageable page) {

        return vehicleService.getPage(vehicleType, plate, model, color, numberChasis, brand, location, headquarterId, page);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    @Operation(
            summary = "Register a new vehicle",
            description = "Creates a new vehicle with the supplied attributes.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Vehicle registration payload",
                    content = @Content(schema = @Schema(implementation = VehicleRequest.class))
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Vehicle successfully created",
                    content = @Content(schema = @Schema(implementation = VehicleResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public ResponseEntity<VehicleResponse> registerOne(@Valid @RequestBody VehicleRequest vehicleRequest) {
        VehicleResponse response = vehicleService.registerOneVehicle(vehicleRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    @Operation(
            summary = "Update an existing vehicle",
            description = "Modifies an existing vehicle identified by its ID.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Vehicle update payload",
                    content = @Content(schema = @Schema(implementation = VehicleRequest.class))
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle successfully updated",
                    content = @Content(schema = @Schema(implementation = VehicleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Vehicle not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<VehicleResponse> updateOne(@Valid @RequestBody VehicleRequest vehicleRequest) {
        VehicleResponse response = vehicleService.updateOneVehicle(vehicleRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    @Operation(
            summary = "Delete a vehicle",
            description = "Performs a delete: the vehicle is removing.",
            parameters = @Parameter(
                    name = "id", in = ParameterIn.PATH, required = true,
                    description = "Unique identifier of the vehicle to delete"
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Vehicle successfully deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid ID",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Vehicle not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<Void> deleteOne(@PathVariable Long id) {
        vehicleService.deleteOneVehicle(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    @Operation(
            summary = "Retrieve a vehicle by ID",
            description = "Returns the vehicle with the specified ID.",
            parameters = @Parameter(
                    name = "id", in = ParameterIn.PATH, required = true,
                    description = "Unique identifier of the desired vehicle"
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle retrieved successfully",
                    content = @Content(schema = @Schema(implementation = VehicleResponse.class))),
            @ApiResponse(responseCode = "404", description = "Vehicle not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<VehicleResponse> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getOne(id));
    }
}
