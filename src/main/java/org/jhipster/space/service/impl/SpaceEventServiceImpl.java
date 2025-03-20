package org.jhipster.space.service.impl;

import java.util.Optional;
import org.jhipster.space.domain.SpaceEvent;
import org.jhipster.space.repository.SpaceEventRepository;
import org.jhipster.space.service.SpaceEventService;
import org.jhipster.space.service.dto.SpaceEventDTO;
import org.jhipster.space.service.mapper.SpaceEventMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link org.jhipster.space.domain.SpaceEvent}.
 */
@Service
@Transactional
public class SpaceEventServiceImpl implements SpaceEventService {

    private static final Logger LOG = LoggerFactory.getLogger(SpaceEventServiceImpl.class);

    private final SpaceEventRepository spaceEventRepository;

    private final SpaceEventMapper spaceEventMapper;

    public SpaceEventServiceImpl(SpaceEventRepository spaceEventRepository, SpaceEventMapper spaceEventMapper) {
        this.spaceEventRepository = spaceEventRepository;
        this.spaceEventMapper = spaceEventMapper;
    }

    @Override
    public SpaceEventDTO save(SpaceEventDTO spaceEventDTO) {
        LOG.debug("Request to save SpaceEvent : {}", spaceEventDTO);
        SpaceEvent spaceEvent = spaceEventMapper.toEntity(spaceEventDTO);
        spaceEvent = spaceEventRepository.save(spaceEvent);
        return spaceEventMapper.toDto(spaceEvent);
    }

    @Override
    public SpaceEventDTO update(SpaceEventDTO spaceEventDTO) {
        LOG.debug("Request to update SpaceEvent : {}", spaceEventDTO);
        SpaceEvent spaceEvent = spaceEventMapper.toEntity(spaceEventDTO);
        spaceEvent = spaceEventRepository.save(spaceEvent);
        return spaceEventMapper.toDto(spaceEvent);
    }

    @Override
    public Optional<SpaceEventDTO> partialUpdate(SpaceEventDTO spaceEventDTO) {
        LOG.debug("Request to partially update SpaceEvent : {}", spaceEventDTO);

        return spaceEventRepository
            .findById(spaceEventDTO.getId())
            .map(existingSpaceEvent -> {
                spaceEventMapper.partialUpdate(existingSpaceEvent, spaceEventDTO);

                return existingSpaceEvent;
            })
            .map(spaceEventRepository::save)
            .map(spaceEventMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SpaceEventDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all SpaceEvents");
        return spaceEventRepository.findAll(pageable).map(spaceEventMapper::toDto);
    }

    public Page<SpaceEventDTO> findAllWithEagerRelationships(Pageable pageable) {
        return spaceEventRepository.findAllWithEagerRelationships(pageable).map(spaceEventMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SpaceEventDTO> findOne(Long id) {
        LOG.debug("Request to get SpaceEvent : {}", id);
        return spaceEventRepository.findOneWithEagerRelationships(id).map(spaceEventMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete SpaceEvent : {}", id);
        spaceEventRepository.deleteById(id);
    }
}
