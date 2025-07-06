package com.codeflow.toolflow.dto.vehiclepart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferablePartVehicleResponse {

    private Long id;
    private String name;
    private Integer availableQuantity;

}
