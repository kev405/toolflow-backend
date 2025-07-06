package com.codeflow.toolflow.dto.vehiclepart;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehiclePartResponse {

    private Long id;
    private String name;
    private String vehicleType;
    private String brand;
    private String model;
    private String description;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
    private List<InventoryDetail> inventories;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InventoryDetail {
        private Long headquarterId;
        private String headquarterName;
        private Boolean vehicleAssociated;
        private Integer quantity;
        private Long vehicleId; // <-- NUEVO CAMPO
    }
}