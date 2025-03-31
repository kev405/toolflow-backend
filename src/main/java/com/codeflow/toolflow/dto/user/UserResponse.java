package com.codeflow.toolflow.dto.user;

import com.codeflow.toolflow.util.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * Data Transfer Object (DTO) representing the response data for a user.
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
     * The roles assigned to the user.
     */
    private List<Role> roles;
}
