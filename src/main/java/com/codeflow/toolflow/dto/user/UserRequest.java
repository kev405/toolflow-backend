package com.codeflow.toolflow.dto.user;

import java.io.Serializable;

import com.codeflow.toolflow.util.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Data Transfer Object (DTO) representing a user request for creating or updating a user.
 * <p>
 * This class is used to carry the user data to be validated when creating or updating a user in the system.
 * It contains fields such as name, username, password, email, phone number, and the role of the user.
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRequest implements Serializable {

    /**
     * The name of the user.
     *
     * @NotNull - This field cannot be null.
     * @Size(min = 4) - The name must be at least 4 characters long.
     */
    @NotNull(message = "Name is required")
    @Size(min = 4, message = "Name must be at least 4 characters long")
    private String name;

    /**
     * The username of the user.
     *
     * @NotNull - This field cannot be null.
     * @Size(min = 4) - The username must be at least 4 characters long.
     */
    @NotNull(message = "Username is required")
    @Size(min = 4, message = "Username must be at least 4 characters long")
    private String username;

    /**
     * The password for the user.
     *
     * @NotNull - This field cannot be null.
     * @Size(min = 8) - The password must be at least 8 characters long.
     */
    @NotNull(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    /**
     * The repeated password for the user (used for verification).
     *
     * @NotNull - This field cannot be null.
     * @Size(min = 8) - The repeated password must be at least 8 characters long.
     */
    @NotNull(message = "Repeated password is required")
    @Size(min = 8, message = "Repeated password must be at least 8 characters long")
    private String repeatedPassword;

    /**
     * The last name of the user.
     *
     * @NotNull - This field cannot be null.
     */
    @NotNull(message = "Last name is required")
    private String lastName;

    /**
     * The phone number of the user.
     *
     * @Pattern - The phone number must be exactly 10 digits.
     */
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    private String phone;

    /**
     * The email address of the user.
     *
     * @NotNull - This field cannot be null.
     * @Email - The email must be in a valid format.
     */
    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    /**
     * The status of the user. Represents whether the user is active or not.
     *
     * The default value is false.
     */
    private boolean status;

    /**
     * The ID of the user who created this user.
     *
     * @NotNull - This field cannot be null.
     */
    @NotNull(message = "Created by ID is required")
    private Long createdBy;

    /**
     * The ID of the user who last updated this user.
     *
     * @NotNull - This field cannot be null.
     */
    @NotNull(message = "Updated by ID is required")
    private Long updatedBy;

    /**
     * The role of the user (e.g., ADMIN, USER).
     *
     * @NotNull - This field cannot be null.
     */
    @NotNull(message = "Role is required")
    private Role role;
}