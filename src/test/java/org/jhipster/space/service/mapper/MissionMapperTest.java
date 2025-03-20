package org.jhipster.space.service.mapper;

import static org.jhipster.space.domain.MissionAsserts.*;
import static org.jhipster.space.domain.MissionTestSamples.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import org.jhipster.space.domain.Mission;
import org.jhipster.space.domain.SpaceEvent;
import org.jhipster.space.domain.enumeration.SpaceEventType;
import org.jhipster.space.service.dto.MissionDTO;
import org.jhipster.space.service.dto.SpaceEventDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MissionMapperTest {

    private MissionMapper missionMapper;

    @BeforeEach
    void setUp() {
        missionMapper = new MissionMapperImpl();
    }

    @Test
    public void testDtoToEntityMapping() {
        MissionDTO missionDTO = new MissionDTO();
        missionDTO.setId(1L);
        missionDTO.setName("Misión de prueba");
        missionDTO.setDescription("Descripción de la misión de prueba");

        Mission mission = missionMapper.toEntity(missionDTO);

        assertEquals(missionDTO.getId(), mission.getId());
        assertEquals(missionDTO.getName(), mission.getName());
        assertEquals(missionDTO.getDescription(), mission.getDescription());
    }

    @Test
    public void testEntityToDtoMapping() {
        Mission mission = new Mission();
        mission.setId(1L);
        mission.setName("Misión de prueba");
        mission.setDescription("Descripción de la misión de prueba");

        MissionDTO missionDTO = missionMapper.toDto(mission);

        assertEquals(mission.getId(), missionDTO.getId());
        assertEquals(mission.getName(), missionDTO.getName());
        assertEquals(mission.getDescription(), missionDTO.getDescription());
    }

    @Test
    public void testNullDtoToEntityMapping() {
        MissionDTO missionDTO = null;
        Mission mission = missionMapper.toEntity(missionDTO);

        assertNull(mission);
    }

    @Test
    public void testNullEntityToDtoMapping() {
        Mission mission = null;
        MissionDTO missionDTO = missionMapper.toDto(mission);

        assertNull(missionDTO);
    }
}
