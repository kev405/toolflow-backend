package com.codeflow.toolflow.dto.transfer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import com.codeflow.toolflow.dto.transfer.validation.AtLeastOneItemRequired;

@Data
@AtLeastOneItemRequired
public class TransferRequest {

    @NotNull(message = "Responsible ID is required")
    private Long responsibleId;

    @NotNull(message = "Origin headquarter ID is required")
    private Long originHeadquarterId;

    @NotNull(message = "Destination headquarter ID is required")
    private Long destinationHeadquarterId;

    private OffsetDateTime transferDate;
    private String notes;

    @Valid
    private List<ToolItem> tools;

    @Valid
    private List<PartItem> vehicleParts;

    private List<Long> vehicles;

    @Data
    public static class ToolItem {
        @NotNull
        private Long toolId;
        @NotNull
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;
    }

    @Data
    public static class PartItem {
        @NotNull
        private Long partId;
        @NotNull
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;
    }
}
