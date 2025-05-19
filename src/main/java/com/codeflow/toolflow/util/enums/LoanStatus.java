package com.codeflow.toolflow.util.enums;

public enum LoanStatus {
    ON_CREATE,                            // EN CREACIÓN
    ORDER,                                // PEDIDO
    ON_LOAN,                              // EN PRÉSTAMO
    FINALIZED,                             // DEVUELTO
    MISSING_FINALIZED,                     // ENTREGADO CON FALTANTES
    DAMAGED_FINALIZED,                     // ENTREGADO CON DAÑOS
    MISSING_AND_DAMAGED_FINALIZED,        // ENTREGADO CON FALTANTES Y DAÑOS
    CANCELLED,                            // CANCELADO
    OVERDUE                               // ATRASADO
}
