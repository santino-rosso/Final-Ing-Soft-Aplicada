package org.jhipster.space.service.mapper;

import static org.jhipster.space.domain.SpaceEventAsserts.*;
import static org.jhipster.space.domain.SpaceEventTestSamples.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import org.jhipster.space.domain.Mission;
import org.jhipster.space.domain.SpaceEvent;
import org.jhipster.space.domain.enumeration.SpaceEventType;
import org.jhipster.space.service.dto.MissionDTO;
import org.jhipster.space.service.dto.SpaceEventDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SpaceEventMapperTest {

    private SpaceEventMapper spaceEventMapper;

    @BeforeEach
    void setUp() {
        spaceEventMapper = new SpaceEventMapperImpl();
    }

    @Test
    public void testDtoToEntityMapping() {
        SpaceEventDTO spaceEventDTO = new SpaceEventDTO();
        spaceEventDTO.setId(1L);
        spaceEventDTO.setName("Evento de prueba");
        spaceEventDTO.setDate(LocalDate.now());
        spaceEventDTO.setDescription("Descripción del evento de prueba");
        spaceEventDTO.setPhoto(new byte[] { 1, 2, 3 });
        spaceEventDTO.setPhotoContentType("image/png");
        spaceEventDTO.setType(SpaceEventType.LAUNCH);

        SpaceEvent spaceEvent = spaceEventMapper.toEntity(spaceEventDTO);

        assertEquals(spaceEventDTO.getId(), spaceEvent.getId());
        assertEquals(spaceEventDTO.getName(), spaceEvent.getName());
        assertEquals(spaceEventDTO.getDate(), spaceEvent.getDate());
        assertEquals(spaceEventDTO.getDescription(), spaceEvent.getDescription());
        assertArrayEquals(spaceEventDTO.getPhoto(), spaceEvent.getPhoto());
        assertEquals(spaceEventDTO.getPhotoContentType(), spaceEvent.getPhotoContentType());
        assertEquals(spaceEventDTO.getType(), spaceEvent.getType());
    }

    @Test
    public void testEntityToDtoMapping() {
        SpaceEvent spaceEvent = new SpaceEvent();
        spaceEvent.setId(1L);
        spaceEvent.setName("Evento de prueba");
        spaceEvent.setDate(LocalDate.now());
        spaceEvent.setDescription("Descripción del evento de prueba");
        spaceEvent.setPhoto(new byte[] { 1, 2, 3 });
        spaceEvent.setPhotoContentType("image/png");
        spaceEvent.setType(SpaceEventType.LAUNCH);

        SpaceEventDTO spaceEventDTO = spaceEventMapper.toDto(spaceEvent);

        assertEquals(spaceEvent.getId(), spaceEventDTO.getId());
        assertEquals(spaceEvent.getName(), spaceEventDTO.getName());
        assertEquals(spaceEvent.getDate(), spaceEventDTO.getDate());
        assertEquals(spaceEvent.getDescription(), spaceEventDTO.getDescription());
        assertArrayEquals(spaceEvent.getPhoto(), spaceEventDTO.getPhoto());
        assertEquals(spaceEvent.getPhotoContentType(), spaceEventDTO.getPhotoContentType());
        assertEquals(spaceEvent.getType(), spaceEventDTO.getType());
    }

    @Test
    public void testNullDtoToEntityMapping() {
        SpaceEventDTO spaceEventDTO = null;
        SpaceEvent spaceEvent = spaceEventMapper.toEntity(spaceEventDTO);

        assertNull(spaceEvent);
    }

    @Test
    public void testNullEntityToDtoMapping() {
        SpaceEvent spaceEvent = null;
        SpaceEventDTO spaceEventDTO = spaceEventMapper.toDto(spaceEvent);

        assertNull(spaceEventDTO);
    }

    @Test
    public void testEntityToDtoMapping_IncludesMission() {
        Mission mission = new Mission();
        mission.setId(42L);
        mission.setName("Misión a Marte");
        mission.setDescription("Misión de exploración al planeta Marte");

        SpaceEvent spaceEvent = new SpaceEvent();
        spaceEvent.setId(1L);
        spaceEvent.setName("Lanzamiento de cohete");
        spaceEvent.setMission(mission);

        SpaceEventDTO spaceEventDTO = spaceEventMapper.toDto(spaceEvent);

        assertNotNull(spaceEventDTO.getMission());
        assertEquals(42L, spaceEventDTO.getMission().getId());
        assertEquals("Misión a Marte", spaceEventDTO.getMission().getName());
        assertEquals("Misión de exploración al planeta Marte", spaceEventDTO.getMission().getDescription());
    }

    @Test
    public void testDtoToEntityMapping_IncludesMission() {
        MissionDTO missionDTO = new MissionDTO();
        missionDTO.setId(42L);
        missionDTO.setName("Misión a Marte");
        missionDTO.setDescription("Misión de exploración al planeta Marte");

        SpaceEventDTO spaceEventDTO = new SpaceEventDTO();
        spaceEventDTO.setId(1L);
        spaceEventDTO.setName("Lanzamiento de cohete");
        spaceEventDTO.setMission(missionDTO);

        SpaceEvent spaceEvent = spaceEventMapper.toEntity(spaceEventDTO);

        assertNotNull(spaceEvent.getMission());
        assertEquals(42L, spaceEvent.getMission().getId());
        assertEquals("Misión a Marte", spaceEvent.getMission().getName());
        assertEquals("Misión de exploración al planeta Marte", spaceEvent.getMission().getDescription());
    }
}
