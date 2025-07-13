package com.codeflow.toolflow.persistence.transfer.repository;

import com.codeflow.toolflow.persistence.transfer.entity.Transfer;
import com.codeflow.toolflow.util.enums.TransferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Repository for managing {@link Transfer} entities.
 */
@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    /**
     * Finds a paginated list of transfers based on a dynamic set of filter criteria.
     * The query uses LEFT JOINs to be able to filter by items within the transfer.
     * DISTINCT is used to avoid duplicate transfers when multiple items match the criteria.
     *
     * @param originId      Optional ID of the origin headquarter.
     * @param destinationId Optional ID of the destination headquarter.
     * @param startDate     Optional start date for the transferDate range.
     * @param endDate       Optional end date for the transferDate range.
     * @param toolIds       Optional list of tool IDs. The transfer must contain at least one of these tools.
     * @param partIds       Optional list of vehicle part IDs. The transfer must contain at least one of these parts.
     * @param vehicleIds    Optional list of vehicle IDs. The transfer must contain at least one of these vehicles.
     * @param pageable      Pagination and sorting information.
     * @return A page of {@link Transfer} entities matching the criteria.
     */
    @Query("SELECT DISTINCT t FROM Transfer t " +
            "LEFT JOIN t.tools tt " +
            "LEFT JOIN t.vehicleParts tvp " +
            "LEFT JOIN t.vehicles tv " +
            "WHERE " +
            "(:originId IS NULL OR t.originHeadquarter.id = :originId) AND " +
            "(:destinationId IS NULL OR t.destinationHeadquarter.id = :destinationId) AND " +
            "(CAST(:startDate as string) IS NULL OR t.transferDate >= :startDate) AND " +
            "(CAST(:endDate as string) IS NULL OR t.transferDate <= :endDate) AND " +
            "(:toolIds IS NULL OR tt.tool.id IN :toolIds) AND " +
            "(:partIds IS NULL OR tvp.vehiclePart.id IN :partIds) AND " +
            "(:vehicleIds IS NULL OR tv.vehicle.id IN :vehicleIds)")
    Page<Transfer> findWithFilters(
            @Param("originId") Long originId,
            @Param("destinationId") Long destinationId,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate,
            @Param("toolIds") List<Long> toolIds,
            @Param("partIds") List<Long> partIds,
            @Param("vehicleIds") List<Long> vehicleIds,
            Pageable pageable);

    @Query("""
                SELECT COUNT(t) > 0
                FROM Transfer t
                WHERE (t.originHeadquarter.id = :headquarterId OR t.destinationHeadquarter.id = :headquarterId)
                  AND t.status = :status
            """)
    boolean existsPendingTransferByHeadquarter(
            @Param("headquarterId") Long headquarterId,
            @Param("status") TransferStatus status
    );
}
