package org.jhipster.space.service;

import java.util.Optional;
import org.jhipster.space.service.dto.SpaceEventDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link org.jhipster.space.domain.SpaceEvent}.
 */
public interface SpaceEventService {
    /**
     * Save a spaceEvent.
     *
     * @param spaceEventDTO the entity to save.
     * @return the persisted entity.
     */
    SpaceEventDTO save(SpaceEventDTO spaceEventDTO);

    /**
     * Updates a spaceEvent.
     *
     * @param spaceEventDTO the entity to update.
     * @return the persisted entity.
     */
    SpaceEventDTO update(SpaceEventDTO spaceEventDTO);

    /**
     * Partially updates a spaceEvent.
     *
     * @param spaceEventDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SpaceEventDTO> partialUpdate(SpaceEventDTO spaceEventDTO);

    /**
     * Get all the spaceEvents.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SpaceEventDTO> findAll(Pageable pageable);

    /**
     * Get all the spaceEvents with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SpaceEventDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" spaceEvent.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SpaceEventDTO> findOne(Long id);

    /**
     * Delete the "id" spaceEvent.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
