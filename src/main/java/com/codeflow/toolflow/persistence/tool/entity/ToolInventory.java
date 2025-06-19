package com.codeflow.toolflow.persistence.tool.entity;

import com.codeflow.toolflow.persistence.headquarter.entity.Headquarter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static jakarta.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "tool_inventory")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ToolInventory {

    public static final String ID_SEQ = "tool_inventory_id_seq";

    @Id
    @GeneratedValue(generator = ID_SEQ, strategy = SEQUENCE)
    @SequenceGenerator(name = ID_SEQ, sequenceName = ID_SEQ, allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tool_id")
    private Tool tool;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "headquarter_id")
    private Headquarter headquarter;

    @NotNull
    private Integer quantity;

    @NotNull
    private Integer available;

    @NotNull
    private Integer onLoan;

    @NotNull
    private Integer damaged;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private Long createdBy;

    @NotNull
    private LocalDateTime updatedAt;

    @NotNull
    private Long updatedBy;
}
