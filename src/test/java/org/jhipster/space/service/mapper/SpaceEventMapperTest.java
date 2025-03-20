package org.jhipster.space.service.mapper;

import static org.jhipster.space.domain.SpaceEventAsserts.*;
import static org.jhipster.space.domain.SpaceEventTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SpaceEventMapperTest {

    private SpaceEventMapper spaceEventMapper;

    @BeforeEach
    void setUp() {
        spaceEventMapper = new SpaceEventMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSpaceEventSample1();
        var actual = spaceEventMapper.toEntity(spaceEventMapper.toDto(expected));
        assertSpaceEventAllPropertiesEquals(expected, actual);
    }
}
