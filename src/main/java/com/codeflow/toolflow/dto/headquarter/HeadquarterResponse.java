package com.codeflow.toolflow.dto.headquarter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing the response data for a headquarter.
 * Contains key information including name, address, and the responsible user.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HeadquarterResponse {

    /**
     * The unique identifier of the headquarter.
     */
    private Long id;

    /**
     * The name of the headquarter.
     */
    private String name;

    /**
     * The physical address of the headquarter.
     */
    private String address;

    /**
     * Indicates whether this headquarter is the main one.
     */
    private boolean main;

    /**
     * Summary information about the user responsible for the headquarter.
     */
    private UserSummary responsible;

    /**
     * Indicates whether the headquarter is active (true) or logically deleted/inactive (false).
     */
    private boolean status;

    /**
     * Summary representation of a user, including only ID and full name.
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserSummary {
        /**
         * The unique identifier of the user.
         */
        private Long id;

        /**
         * The full name of the user (e.g., "Jane Smith").
         */
        private String fullName;
    }
}
