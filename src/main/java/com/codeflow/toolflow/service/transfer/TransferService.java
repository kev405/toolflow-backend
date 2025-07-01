package com.codeflow.toolflow.service.transfer;

import com.codeflow.toolflow.dto.transfer.TransferRequest;
import com.codeflow.toolflow.dto.transfer.TransferResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransferService {

    /**
     * Creates a new transfer request with PENDING status after validating the request.
     *
     * @param request DTO with all the transfer details.
     * @return The created transfer with its details and PENDING status.
     */
    TransferResponse createTransfer(TransferRequest request);

    /**
     * Processes a transfer by changing its status to ACCEPTED.
     * This triggers all the inventory movements and vehicle updates.
     * This action is final and cannot be undone.
     *
     * @param transferId The ID of the transfer to accept.
     * @return The updated transfer with ACCEPTED status.
     */
    TransferResponse acceptTransfer(Long transferId);

    /**
     * Cancels a pending transfer request.
     * This action is final.
     *
     * @param transferId The ID of the transfer to cancel.
     * @return The updated transfer with CANCELLED status.
     */
    TransferResponse cancelTransfer(Long transferId);

    /**
     * Retrieves a single transfer by its ID.
     *
     * @param transferId The ID of the transfer.
     * @return The transfer details.
     */
    TransferResponse getTransferById(Long transferId);

    /**
     * Retrieves a paginated list of all transfers.
     *
     * @param pageable Pagination and sorting information.
     * @return A page of transfers.
     */
    Page<TransferResponse> getAllTransfers(Pageable pageable);
}
