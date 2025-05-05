package com.codeflow.toolflow.controller.vehicle;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.codeflow.toolflow.dto.ApiError;
import com.codeflow.toolflow.dto.vehicle.VehicleRequest;
import com.codeflow.toolflow.dto.vehicle.VehicleResponse;
import com.codeflow.toolflow.service.vehicle.VehicleService;

@RestController
@RequestMapping("/vehicle")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;


    @GetMapping(path = "/findBy")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    @Operation(
            summary = "Search Vehicles",
            description = "Returns a paginated list of vehicles filtered by one or more optional attributes.",
            parameters = {
                    @Parameter(name = "vehicleType",  in = ParameterIn.QUERY, description = "Vehicle type (e.g. Car, Truck)", required = false),
                    @Parameter(name = "plate",        in = ParameterIn.QUERY, description = "License-plate number",          required = false),
                    @Parameter(name = "model",        in = ParameterIn.QUERY, description = "Vehicle model",                 required = false),
                    @Parameter(name = "color",        in = ParameterIn.QUERY, description = "Exterior color",                required = false),
                    @Parameter(name = "numberChasis", in = ParameterIn.QUERY, description = "Chassis/VIN number",           required = false),
                    @Parameter(name = "brand",        in = ParameterIn.QUERY, description = "Manufacturer brand",            required = false),
                    @Parameter(name = "location",     in = ParameterIn.QUERY, description = "Current location",             required = false)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description   = "Page of vehicles retrieved successfully",
                    content       = @Content(array = @ArraySchema(schema = @Schema(implementation = VehicleResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description   = "Invalid filter or pagination parameters",
                    content       = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public Page<VehicleResponse> findBy(@RequestParam(required = false) String vehicleType,
                                        @RequestParam(required = false) String plate,
                                        @RequestParam(required = false) String model,
                                        @RequestParam(required = false) String color,
                                        @RequestParam(required = false) String numberChasis,
                                        @RequestParam(required = false) String brand,
                                        @RequestParam(required = false) String location,
                                        Pageable page) {
        return vehicleService.getPage(vehicleType, plate, model, color, numberChasis,
                brand, location, page);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    @Operation(
            summary = "Register New Vehicle",
            description = "Creates a new tool with the provided tool details.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Vehicle registration payload. Contains data such as name, brand, quantity, availability, damaged, onLoan, consumable, etc.",
                    content = @Content(schema = @Schema(implementation = VehicleRequest.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Vehicle successfully created", content = @Content(schema = @Schema(implementation = VehicleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<VehicleResponse> registerOne(@Valid @RequestBody VehicleRequest vehicleRequest) {
        VehicleResponse response = vehicleService.registerOneVehicle(vehicleRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    @Operation(
            summary = "Update Vehicle",
            description = "Updates an existing tool using the provided details.",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID of the tool to be updated", required = true)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Vehicle update payload.",
                    content = @Content(schema = @Schema(implementation = VehicleRequest.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicle successfully updated", content = @Content(schema = @Schema(implementation = VehicleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Vehicle not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<VehicleResponse> updateOne(@PathVariable Long id, @Valid @RequestBody VehicleRequest vehicleRequest) {
        VehicleResponse response = vehicleService.updateOneVehicle(vehicleRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    @Operation(
            summary = "Delete Vehicle",
            description = "Deletes (soft) a tool by marking its status as false.",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID of the tool to be deleted", required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Vehicle successfully deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid ID", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Vehicle not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<Void> deleteOne(@PathVariable Long id) {
        vehicleService.deleteOneVehicle(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    @Operation(
            summary = "Get Vehicle by ID",
            description = "Fetches a tool using its unique identifier.",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, description = "Vehicle ID", required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicle retrieved successfully", content = @Content(schema = @Schema(implementation = VehicleResponse.class))),
            @ApiResponse(responseCode = "404", description = "Vehicle not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<VehicleResponse> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getOne(id));
    }

}
