package com.codeflow.toolflow.dto.vehicle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferableVehicleResponse {

    private Long id;
    private String name;
    private Integer availableQuantity;
    private String vehicleType;
}
