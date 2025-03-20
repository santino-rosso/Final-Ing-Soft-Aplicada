package org.jhipster.space.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jhipster.space.domain.MissionTestSamples.*;
import static org.jhipster.space.domain.SpaceEventTestSamples.*;

import org.jhipster.space.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SpaceEventTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SpaceEvent.class);
        SpaceEvent spaceEvent1 = getSpaceEventSample1();
        SpaceEvent spaceEvent2 = new SpaceEvent();
        assertThat(spaceEvent1).isNotEqualTo(spaceEvent2);

        spaceEvent2.setId(spaceEvent1.getId());
        assertThat(spaceEvent1).isEqualTo(spaceEvent2);

        spaceEvent2 = getSpaceEventSample2();
        assertThat(spaceEvent1).isNotEqualTo(spaceEvent2);
    }

    @Test
    void missionTest() {
        SpaceEvent spaceEvent = getSpaceEventRandomSampleGenerator();
        Mission missionBack = getMissionRandomSampleGenerator();

        spaceEvent.setMission(missionBack);
        assertThat(spaceEvent.getMission()).isEqualTo(missionBack);

        spaceEvent.mission(null);
        assertThat(spaceEvent.getMission()).isNull();
    }
}
