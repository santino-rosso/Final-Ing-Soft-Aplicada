package org.jhipster.space.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.jhipster.space.domain.Mission;
import org.jhipster.space.repository.MissionRepository;
import org.jhipster.space.service.MissionService;
import org.jhipster.space.service.dto.MissionDTO;
import org.jhipster.space.service.mapper.MissionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link org.jhipster.space.domain.Mission}.
 */
@Service
@Transactional
public class MissionServiceImpl implements MissionService {

    private static final Logger LOG = LoggerFactory.getLogger(MissionServiceImpl.class);

    private final MissionRepository missionRepository;

    private final MissionMapper missionMapper;

    public MissionServiceImpl(MissionRepository missionRepository, MissionMapper missionMapper) {
        this.missionRepository = missionRepository;
        this.missionMapper = missionMapper;
    }

    @Override
    public MissionDTO save(MissionDTO missionDTO) {
        LOG.debug("Request to save Mission : {}", missionDTO);
        Mission mission = missionMapper.toEntity(missionDTO);
        mission = missionRepository.save(mission);
        return missionMapper.toDto(mission);
    }

    @Override
    public MissionDTO update(MissionDTO missionDTO) {
        LOG.debug("Request to update Mission : {}", missionDTO);
        Mission mission = missionMapper.toEntity(missionDTO);
        mission = missionRepository.save(mission);
        return missionMapper.toDto(mission);
    }

    @Override
    public Optional<MissionDTO> partialUpdate(MissionDTO missionDTO) {
        LOG.debug("Request to partially update Mission : {}", missionDTO);

        return missionRepository
            .findById(missionDTO.getId())
            .map(existingMission -> {
                missionMapper.partialUpdate(existingMission, missionDTO);

                return existingMission;
            })
            .map(missionRepository::save)
            .map(missionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MissionDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Missions");
        return missionRepository.findAll(pageable).map(missionMapper::toDto);
    }

    /**
     *  Get all the missions where SpaceEvent is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<MissionDTO> findAllWhereSpaceEventIsNull() {
        LOG.debug("Request to get all missions where SpaceEvent is null");
        return StreamSupport.stream(missionRepository.findAll().spliterator(), false)
            .filter(mission -> mission.getSpaceEvent() == null)
            .map(missionMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MissionDTO> findOne(Long id) {
        LOG.debug("Request to get Mission : {}", id);
        return missionRepository.findById(id).map(missionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Mission : {}", id);
        missionRepository.deleteById(id);
    }
}
