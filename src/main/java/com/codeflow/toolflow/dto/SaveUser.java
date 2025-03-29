package com.codeflow.toolflow.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SaveUser implements Serializable {

    @Size(min = 4)
    private String name;

    private String username;

    @Size(min = 8)
    private String password;

    @NotNull
    private String lastName;

    private Integer phone;

    @NotNull
    private String email;

    @NotNull
    private String status;

    @Size(min = 8)
    private String repeatedPassword;

    private LocalDateTime createdAt;

    @NotNull
    private Long createdBy;

    @NotNull
    private LocalDateTime updatedAt;

    @NotNull
    private Long updatedBy;

}
