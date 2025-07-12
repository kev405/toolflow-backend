package com.codeflow.toolflow.dto.transfer;

import lombok.Builder;
import lombok.Data;
import java.time.OffsetDateTime;
import java.util.List;
import com.codeflow.toolflow.util.enums.TransferStatus;

@Data
@Builder
public class TransferResponse {
    private Long id;
    private UserSummary responsible;
    private HeadquarterSummary originHeadquarter;
    private HeadquarterSummary destinationHeadquarter;
    private OffsetDateTime transferDate;
    private TransferStatus transferStatus;
    private String notes;
    private OffsetDateTime createdAt;
    private List<ToolItemResponse> tools;
    private List<PartItemResponse> vehicleParts;
    private List<VehicleSummary> vehicles;

    @Data
    @Builder
    public static class UserSummary {
        private Long id;
        private String username;
    }

    @Data
    @Builder
    public static class HeadquarterSummary {
        private Long id;
        private String name;
    }

    @Data
    @Builder
    public static class ToolItemResponse {
        private Long toolId;
        private String toolName;
        private Integer quantity;
    }

    @Data
    @Builder
    public static class PartItemResponse {
        private Long partId;
        private String partName;
        private Integer quantity;
    }

    @Data
    @Builder
    public static class VehicleSummary {
        private Long vehicleId;
        private String plate;
        private String model;
    }
}
