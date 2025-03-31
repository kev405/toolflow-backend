package com.codeflow.toolflow.persistence.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static jakarta.persistence.GenerationType.SEQUENCE;

/**
 * Represents a user entity in the system.
 */
@Entity
@Table(name = "toolflow_user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    /**
     * The sequence name used for the `id` field in the `User` entity.
     * This constant defines the database sequence `toolflow_user_id_seq`
     * used for generating unique identifiers for user records.
     */
    public static final String ID_SEQ = "toolflow_user_id_seq";

    /**
     * Represents the unique identifier for a user.
     * This field is automatically generated based on a
     * database sequence defined by the constant `ID_SEQ`.
     * It is annotated with `@Id` to signify it as the primary key,
     * and with `@GeneratedValue` utilizing the `SEQUENCE` generation strategy.
     * The `@SequenceGenerator` defines the sequence details, including
     * the sequence name and allocation size.
     * The field is also included in the `equals` and `hashCode` methods
     * through the `@EqualsAndHashCode.Include` annotation.
     */
    @Id
    @GeneratedValue(generator = ID_SEQ, strategy = SEQUENCE)
    @SequenceGenerator(name = ID_SEQ, sequenceName = ID_SEQ, allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Represents the username of a user.
     * This field is a unique identifier for a user within the system.
     * It is annotated with `@Column(unique = true)` to enforce
     * uniqueness at the database level, ensuring no two users share the same username.
     */
    @Column(unique = true)
    private String username;

    /**
     * Represents the name of the user.
     * This field is mandatory and cannot be null.
     */
    @NotNull
    private String name;

    /**
     * Represents the last name of the user.
     * This field is mandatory and cannot be null.
     */
    @NotNull
    private String lastName;

    /**
     * Represents the phone number of the user.
     * This field is optional and may contain the user's contact number.
     */
    private Integer phone;

    /**
     * Represents the email address of the user.
     * This field is mandatory and cannot be null.
     * It is expected to store a valid email format for user identification or communication.
     */
    @NotNull
    private String email;

    /**
     * Indicates the status of the user.
     * This field is mandatory and cannot be null.
     * It typically represents whether the user is active or inactive in the system.
     */
    @NotNull
    private boolean status;

    /**
     * Represents the password of the user.
     * This field is mandatory and cannot be null.
     * It is essential for user authentication and must be stored securely.
     */
    @NotNull
    private String password;

    /**
     * Represents the timestamp when the user entity was created.
     * This field is mandatory and cannot be null.
     */
    @NotNull
    private LocalDateTime createdAt;

    /**
     * Represents the identifier of the user or entity that created this user entity.
     * This field is mandatory and cannot be null.
     * It is used for tracking the origin of the creation event in the system.
     */
    @NotNull
    private Long createdBy;

    /**
     * Represents the timestamp when the user entity was last updated.
     * This field is mandatory and cannot be null.
     * It is used to track the most recent update event for the user entity in the system.
     */
    @NotNull
    private LocalDateTime updatedAt;

    /**
     * Represents the identifier of the user or entity that last updated this user entity.
     * This field is mandatory and cannot be null.
     * It is used for tracking the origin of the most recent update event in the system.
     */
    @NotNull
    private Long updatedBy;

    /**
     * Represents the collection of roles associated with the user.
     * This relationship is defined as a one-to-many association where each user can have multiple roles.
     * It is managed using a bidirectional mapping with the `toolflowUser` field in the `UserRole` entity.
     * <p>
     * Cascade operations are enabled for all types, meaning any operation (e.g., persist, merge, remove)
     * performed on a `User` entity will cascade to its associated roles.
     * Orphan removal is enabled, ensuring that any `UserRole` entities detached from the list
     * are automatically removed from the database.
     * The roles are eagerly fetched, meaning they are loaded alongside the user entity whenever it is accessed.
     */
    @OneToMany(mappedBy = "toolflowUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<UserRole> userRoles = new ArrayList<>();
}
