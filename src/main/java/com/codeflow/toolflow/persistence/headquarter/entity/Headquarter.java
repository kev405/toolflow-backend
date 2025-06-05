package com.codeflow.toolflow.persistence.headquarter.entity;

import com.codeflow.toolflow.persistence.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static jakarta.persistence.GenerationType.SEQUENCE;

/**
 * Represents a headquarter (sede) in the system.
 * Each headquarter includes its name, address, responsible user, and system flags.
 */
@Entity
@Table(name = "headquarter")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Headquarter {

    /**
     * The sequence name used for the `id` field in the `Headquarter` entity.
     */
    public static final String ID_SEQ = "headquarter_id_seq";

    /**
     * Unique identifier for the headquarter.
     */
    @Id
    @GeneratedValue(generator = ID_SEQ, strategy = SEQUENCE)
    @SequenceGenerator(name = ID_SEQ, sequenceName = ID_SEQ, allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Name of the headquarter.
     */
    @NotBlank
    @Column(nullable = false, length = 150)
    private String name;

    /**
     * Physical address of the headquarter.
     */
    @NotBlank
    @Column(nullable = false, length = 255)
    private String address;

    /**
     * Indicates if this headquarter is the main one (non-deletable).
     */
    @NotNull
    @Column(nullable = false)
    private Boolean main;

    /**
     * User responsible for this headquarter.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "responsible_id", referencedColumnName = "id", nullable = true)
    private User responsible;

    /**
     * Timestamp of when the headquarter was created.
     */
    @NotNull
    private LocalDateTime createdAt;

    /**
     * ID of the user who created this headquarter record.
     */
    @NotNull
    private Long createdBy;

    /**
     * Timestamp of the last update to the headquarter.
     */
    @NotNull
    private LocalDateTime updatedAt;

    /**
     * ID of the user who last updated this headquarter record.
     */
    @NotNull
    private Long updatedBy;

    /**
     * Indicates whether this headquarter is active.
     * If false, the headquarter is considered logically deleted.
     */
    @NotNull
    @Column(nullable = false)
    private Boolean status;
}
