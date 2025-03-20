package org.jhipster.space.service.mapper;

import static org.jhipster.space.domain.MissionAsserts.*;
import static org.jhipster.space.domain.MissionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MissionMapperTest {

    private MissionMapper missionMapper;

    @BeforeEach
    void setUp() {
        missionMapper = new MissionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMissionSample1();
        var actual = missionMapper.toEntity(missionMapper.toDto(expected));
        assertMissionAllPropertiesEquals(expected, actual);
    }
}
