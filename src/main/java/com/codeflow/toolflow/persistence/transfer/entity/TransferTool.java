package com.codeflow.toolflow.persistence.transfer.entity;

import com.codeflow.toolflow.persistence.tool.entity.Tool; // Assuming this entity exists
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "transfer_tools")
@Data
public class TransferTool {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transfer_id", nullable = false)
    private Transfer transfer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tool_id", nullable = false)
    private Tool tool;

    @Column(nullable = false)
    private Integer quantity;
}
