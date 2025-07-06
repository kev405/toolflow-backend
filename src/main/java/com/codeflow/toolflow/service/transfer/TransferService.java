package com.codeflow.toolflow.service.transfer;

import java.util.List;
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

//    /**
//     * Retrieves a paginated list of all transfers.
//     *
//     * @param pageable Pagination and sorting information.
//     * @return A page of transfers.
//     */
//    Page<TransferResponse> getAllTransfers(Pageable pageable);

    /**
     * Retrieves a paginated list of transfers based on a set of optional filters.
     *
     * @param originId Optional ID of the origin headquarter.
     * @param destinationId Optional ID of the destination headquarter.
     * @param transferDate Optional specific date for the transfer. Can be null.
     * @param toolIds Optional list of tool IDs to filter by.
     * @param partIds Optional list of vehicle part IDs to filter by.
     * @param vehicleIds Optional list of vehicle IDs to filter by.
     * @param pageable Pagination and sorting information.
     * @return A page of transfer DTOs.
     */
    Page<TransferResponse> getAllTransfers(
            Long originId,
            Long destinationId,
            String transferDate, // Recibimos la fecha como String desde el controller
            List<Long> toolIds,
            List<Long> partIds,
            List<Long> vehicleIds,
            Pageable pageable
    );
}
