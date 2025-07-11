package com.codeflow.toolflow.service.headquarter;

import com.codeflow.toolflow.dto.headquarter.HeadquarterRequest;
import com.codeflow.toolflow.dto.headquarter.HeadquarterResponse;
import com.codeflow.toolflow.persistence.headquarter.entity.Headquarter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interface defining business operations related to headquarter management.
 */
public interface HeadquarterService {

    /**
     * Registers a new headquarter in the system.
     *
     * @param request the headquarter request payload
     * @return a {@link HeadquarterResponse} representing the newly created headquarter
     */
    HeadquarterResponse registerOne(HeadquarterRequest request);

    /**
     * Updates an existing headquarter.
     *
     * @param id      ID of the headquarter to update
     * @param request updated data
     * @return the updated {@link HeadquarterResponse}
     */
    HeadquarterResponse updateOne(Long id, HeadquarterRequest request);

    /**
     * Logically deletes (or disables) a headquarter.
     *
     * @param id ID of the headquarter to delete
     */
    void deleteOne(Long id);

    /**
     * Retrieves a headquarter by its ID.
     *
     * @param id the ID to look up
     * @return a {@link HeadquarterResponse}
     */
    HeadquarterResponse getOne(Long id);

    /**
     * Retrieves a headquarter by its ID.
     *
     * @param id the ID to look up
     * @return a {@link Headquarter}
     */
    Headquarter getOneEntity(Long id);

    /**
     * Retrieves all offices (e.g., fixed headquarters).
     *
     * @return list of {@link HeadquarterResponse}
     */
    List<HeadquarterResponse> getAll();

    /**
     * Gets a paginated list of headquarters with optional filtering.
     *
     * @param pageable      pagination info
     * @return a page of {@link HeadquarterResponse}
     */
    Page<HeadquarterResponse> getPage(Pageable pageable);

    /**
     * Retrieves the main headquarter, which is typically the primary office or location.
     *
     * @return the main {@link HeadquarterResponse}
     */
    Headquarter getMainHeadquarter();
}
