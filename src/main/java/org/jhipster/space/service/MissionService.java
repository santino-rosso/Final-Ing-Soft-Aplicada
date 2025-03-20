package org.jhipster.space.service;

import java.util.List;
import java.util.Optional;
import org.jhipster.space.service.dto.MissionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link org.jhipster.space.domain.Mission}.
 */
public interface MissionService {
    /**
     * Save a mission.
     *
     * @param missionDTO the entity to save.
     * @return the persisted entity.
     */
    MissionDTO save(MissionDTO missionDTO);

    /**
     * Updates a mission.
     *
     * @param missionDTO the entity to update.
     * @return the persisted entity.
     */
    MissionDTO update(MissionDTO missionDTO);

    /**
     * Partially updates a mission.
     *
     * @param missionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<MissionDTO> partialUpdate(MissionDTO missionDTO);

    /**
     * Get all the missions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<MissionDTO> findAll(Pageable pageable);

    /**
     * Get all the MissionDTO where SpaceEvent is {@code null}.
     *
     * @return the {@link List} of entities.
     */
    List<MissionDTO> findAllWhereSpaceEventIsNull();

    /**
     * Get the "id" mission.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<MissionDTO> findOne(Long id);

    /**
     * Delete the "id" mission.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
