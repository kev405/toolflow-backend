package com.codeflow.toolflow.dto.tool;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * Data Transfer Object (DTO) representing a simplified response data for a tool,
 * useful for selects or lightweight listings.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ToolSimpleResponse implements Serializable {

    /**
     * The unique identifier of the tool.
     */
    private Long id;

    /**
     * The name of the tool.
     */
    private String toolName;

    /**
     * The number of units currently available for use.
     */
    private Integer available;

}
