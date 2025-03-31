package com.codeflow.toolflow.controller.auth;

import com.codeflow.toolflow.dto.ApiError;
import com.codeflow.toolflow.dto.auth.AuthenticationRequest;
import com.codeflow.toolflow.dto.auth.AuthenticationResponse;
import com.codeflow.toolflow.dto.auth.UserLogin;
import com.codeflow.toolflow.service.auth.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @Operation(
            summary = "Validate JWT Token",
            description = "Validates the provided JWT token and returns whether it is valid or not.",
            parameters = {
                    @Parameter(
                            name = "jwt",
                            in = ParameterIn.QUERY,
                            description = "JWT token to be validated",
                            required = true,
                            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Token validation result",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Boolean.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid token supplied",
                            content = @Content(schema = @Schema(implementation = ApiError.class))
                    )
            }
    )
    @GetMapping("/validate-token")
    public ResponseEntity<Boolean> validate(@RequestParam String jwt) {
        boolean isTokenValid = authenticationService.validateToken(jwt);
        return ResponseEntity.ok(isTokenValid);
    }

    @Operation(
            summary = "Authenticate User",
            description = "Authenticates a user using the provided credentials and returns an authentication response with token details.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Authentication request payload",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User authenticated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AuthenticationResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid credentials supplied",
                            content = @Content(schema = @Schema(implementation = ApiError.class))
                    )
            }
    )
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody @Valid AuthenticationRequest authenticationRequest) {

        AuthenticationResponse rsp = authenticationService.login(authenticationRequest);
        return ResponseEntity.ok(rsp);

    }

    @Operation(
            summary = "Get Logged In User Profile",
            description = "Retrieves the profile information of the currently authenticated user.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User profile retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserLogin.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized or invalid token",
                            content = @Content(schema = @Schema(implementation = ApiError.class))
                    )
            }
    )
    @GetMapping("/profile")
    public ResponseEntity<UserLogin> findMyProfile() {
        UserLogin user = authenticationService.findLoggedInUser();
        return ResponseEntity.ok(user);
    }

}
