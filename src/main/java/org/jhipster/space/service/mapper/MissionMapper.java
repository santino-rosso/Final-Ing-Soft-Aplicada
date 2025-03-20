package org.jhipster.space.service.mapper;

import org.jhipster.space.domain.Mission;
import org.jhipster.space.service.dto.MissionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Mission} and its DTO {@link MissionDTO}.
 */
@Mapper(componentModel = "spring")
public interface MissionMapper extends EntityMapper<MissionDTO, Mission> {}
