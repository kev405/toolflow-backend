package com.codeflow.toolflow.dto.tool;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ToolInventoryResponse {
    private Long id;
    private Long headquarterId;
    private String name;
    private Integer quantity;
    private Integer available;
    private Integer onLoan;
    private Integer damaged;
    private boolean main;
}
