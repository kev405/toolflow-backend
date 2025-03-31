package com.codeflow.toolflow.persistence.user.entity;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static jakarta.persistence.GenerationType.SEQUENCE;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;


@Entity
@Table(name = "toolflow_user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    public static final String ID_SEQ = "toolflow_user_id_seq";

    @Id
    @GeneratedValue(generator = ID_SEQ, strategy = SEQUENCE)
    @SequenceGenerator(name = ID_SEQ, sequenceName = ID_SEQ, allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(unique = true)
    private String username;

    @NotNull
    private String name;

    @NotNull
    private String lastName;

    private Integer phone;

    @NotNull
    private String email;

    @NotNull
    private boolean status;

    @NotNull
    private String password;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private Long createdBy;

    @NotNull
    private LocalDateTime updatedAt;

    @NotNull
    private Long updatedBy;

}
