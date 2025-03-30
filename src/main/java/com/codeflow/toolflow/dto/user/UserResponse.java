package com.codeflow.toolflow.dto.user;

import java.io.Serializable;

import com.codeflow.toolflow.util.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Data Transfer Object (DTO) representing the response data for a user.
 * <p>
 * This class is used to return the user information after a user has been created or updated.
 * It contains details such as the user's ID, name, username, last name, email, phone number, and role.
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponse implements Serializable {

    /**
     * The unique identifier of the user.
     */
    private Long id;

    /**
     * The first name of the user.
     */
    private String name;

    /**
     * The username of the user.
     */
    private String username;

    /**
     * The last name of the user.
     */
    private String lastName;

    /**
     * The email address of the user.
     */
    private String email;

    /**
     * The phone number of the user.
     */
    private String phone;

    /**
     * The role of the user (e.g., ADMIN, USER).
     */
    private Role role;
}
