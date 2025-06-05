package com.codeflow.toolflow.dto.headquarter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing the request data to create or update a headquarter.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HeadquarterRequest {

    /**
     * The name of the headquarter.
     */
    @NotBlank(message = "Name is required")
    private String name;

    /**
     * The physical address of the headquarter.
     */
    @NotBlank(message = "Address is required")
    private String address;

    /**
     * The ID of the user responsible for this headquarter.
     */
    @NotNull(message = "Responsible user ID is required")
    private Long responsibleId;
}
