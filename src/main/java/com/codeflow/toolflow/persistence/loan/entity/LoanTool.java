package com.codeflow.toolflow.persistence.loan.entity;

import com.codeflow.toolflow.persistence.tool.entity.Tool;
import com.codeflow.toolflow.persistence.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static jakarta.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "loan_tool")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoanTool {

    public static final String ID_SEQ = "loan_tool_id_seq";

    @Id
    @GeneratedValue(generator = ID_SEQ, strategy = SEQUENCE)
    @SequenceGenerator(name = ID_SEQ, sequenceName = ID_SEQ, allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id")
    private Loan loan;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tool_id")
    private Tool tool;

    @ManyToOne
    @JoinColumn(name = "responsible_id")
    private User responsible;

    @NotNull
    private Integer requested;

    @NotNull
    private Integer loaned;

    @NotNull
    private Integer delivered;

    @NotNull
    private Integer damaged;

    private String notes;
}
