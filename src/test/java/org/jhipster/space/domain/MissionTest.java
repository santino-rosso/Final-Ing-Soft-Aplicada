package org.jhipster.space.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jhipster.space.domain.MissionTestSamples.*;
import static org.jhipster.space.domain.SpaceEventTestSamples.*;

import org.jhipster.space.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MissionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Mission.class);
        Mission mission1 = getMissionSample1();
        Mission mission2 = new Mission();
        assertThat(mission1).isNotEqualTo(mission2);

        mission2.setId(mission1.getId());
        assertThat(mission1).isEqualTo(mission2);

        mission2 = getMissionSample2();
        assertThat(mission1).isNotEqualTo(mission2);
    }

    @Test
    void spaceEventTest() {
        Mission mission = getMissionRandomSampleGenerator();
        SpaceEvent spaceEventBack = getSpaceEventRandomSampleGenerator();

        mission.setSpaceEvent(spaceEventBack);
        assertThat(mission.getSpaceEvent()).isEqualTo(spaceEventBack);
        assertThat(spaceEventBack.getMission()).isEqualTo(mission);

        mission.spaceEvent(null);
        assertThat(mission.getSpaceEvent()).isNull();
        assertThat(spaceEventBack.getMission()).isNull();
    }
}
