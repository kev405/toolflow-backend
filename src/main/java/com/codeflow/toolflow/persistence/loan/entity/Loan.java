package com.codeflow.toolflow.persistence.loan.entity;

import com.codeflow.toolflow.persistence.user.entity.User;
import com.codeflow.toolflow.util.enums.LoanStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static jakarta.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "loan")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Loan {

    public static final String ID_SEQ = "loan_id_seq";

    @Id
    @GeneratedValue(generator = ID_SEQ, strategy = SEQUENCE)
    @SequenceGenerator(name = ID_SEQ, sequenceName = ID_SEQ, allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @ManyToOne
    @JoinColumn(name = "responsible_id")
    private User responsible;

    private String notes;

    @Enumerated(EnumType.STRING)
    private LoanStatus loanStatus;

    @NotNull
    private LocalDate dueDate;

    private LocalDate receivedDate;

    @NotNull
    private Boolean status;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private Long createdBy;

    @NotNull
    private LocalDateTime updatedAt;

    @NotNull
    private Long updatedBy;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<LoanTool> loanTools = new ArrayList<>();
}