package org.jhipster.space.service.mapper;

import org.jhipster.space.domain.Mission;
import org.jhipster.space.domain.SpaceEvent;
import org.jhipster.space.service.dto.MissionDTO;
import org.jhipster.space.service.dto.SpaceEventDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SpaceEvent} and its DTO {@link SpaceEventDTO}.
 */
@Mapper(componentModel = "spring")
public interface SpaceEventMapper extends EntityMapper<SpaceEventDTO, SpaceEvent> {
    @Mapping(target = "mission", source = "mission", qualifiedByName = "missionName")
    SpaceEventDTO toDto(SpaceEvent s);

    @Named("missionName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    MissionDTO toDtoMissionName(Mission mission);
}
