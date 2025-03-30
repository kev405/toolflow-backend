package com.codeflow.toolflow.controller.user;

import com.codeflow.toolflow.dto.user.UserRequest;
import com.codeflow.toolflow.dto.user.UserResponse;
import com.codeflow.toolflow.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing user operations.
 *
 * This controller provides endpoints for handling user-related actions,
 * including registering new users, as well as providing authorization requirements
 * for secure operations.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(
            summary = "Register New User",
            description = "Registers a new user in the system using the provided user details. Returns the details of the newly created user.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User registration payload. Contains information such as name, username, password, repeatedPassword, lastName, phone, email, status, createdBy, updatedBy, and role.",
                    content = @Content(
                            schema = @Schema(implementation = UserRequest.class),
                            examples = {
                                    @ExampleObject(value = "{\n"
                                            + "  \"name\": \"John\",\n"
                                            + "  \"username\": \"john_doe\",\n"
                                            + "  \"password\": \"password123\",\n"
                                            + "  \"repeatedPassword\": \"password123\",\n"
                                            + "  \"lastName\": \"Doe\",\n"
                                            + "  \"phone\": \"1234567890\",\n"
                                            + "  \"email\": \"john.doe@example.com\",\n"
                                            + "  \"status\": true,\n"
                                            + "  \"createdBy\": 1,\n"
                                            + "  \"updatedBy\": 1,\n"
                                            + "  \"role\": \"USER\"\n"
                                            + "}")
                            }
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", description = "Invalid user details provided"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    @PostMapping("")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<UserResponse> registerOne(@RequestBody @Valid UserRequest userRequest) {
        UserResponse user = userService.registerOneCustomer(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}
