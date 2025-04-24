package com.codeflow.toolflow.dto.category;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * Data Transfer Object (DTO) representing the response data for a tool category.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryResponse implements Serializable {

    /**
     * The unique identifier of the category.
     */
    private Long id;

    /**
     * The name of the category.
     */
    private String name;

    /**
     * The current status of the category (true = active, false = inactive).
     */
    private Boolean status;

    /**
     * The timestamp when the category was created.
     */
    private LocalDateTime createdAt;

    /**
     * The timestamp when the category was last updated.
     */
    private LocalDateTime updatedAt;
}
