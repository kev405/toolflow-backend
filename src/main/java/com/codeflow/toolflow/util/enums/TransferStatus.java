package com.codeflow.toolflow.util.enums;

/**
 * Represents the status of a transfer request.
 * <ul>
 * <li><b>PENDING:</b> The transfer has been requested but not yet processed. It can be modified or cancelled.</li>
 * <li><b>ACCEPTED:</b> The transfer has been approved and all inventory changes have been executed. It cannot be modified.</li>
 * <li><b>CANCELLED:</b> The transfer has been cancelled and no inventory changes were made. It cannot be modified.</li>
 * </ul>
 */
public enum TransferStatus {
    PENDING,
    ACCEPTED,
    CANCELLED
}
