package org.jhipster.space.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.jhipster.space.domain.MissionAsserts.*;
import static org.jhipster.space.web.rest.TestUtil.createUpdateProxyForBean;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.jhipster.space.IntegrationTest;
import org.jhipster.space.domain.Mission;
import org.jhipster.space.repository.MissionRepository;
import org.jhipster.space.service.dto.MissionDTO;
import org.jhipster.space.service.mapper.MissionMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link MissionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MissionResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/missions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private MissionMapper missionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMissionMockMvc;

    private Mission mission;

    private Mission insertedMission;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Mission createEntity() {
        return new Mission().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Mission createUpdatedEntity() {
        return new Mission().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
    }

    @BeforeEach
    public void initTest() {
        mission = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedMission != null) {
            missionRepository.delete(insertedMission);
            insertedMission = null;
        }
    }

    @Test
    @Transactional
    void createMission() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Mission
        MissionDTO missionDTO = missionMapper.toDto(mission);
        var returnedMissionDTO = om.readValue(
            restMissionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(missionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MissionDTO.class
        );

        // Validate the Mission in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMission = missionMapper.toEntity(returnedMissionDTO);
        assertMissionUpdatableFieldsEquals(returnedMission, getPersistedMission(returnedMission));

        insertedMission = returnedMission;
    }

    @Test
    @Transactional
    void createMissionWithExistingId() throws Exception {
        // Create the Mission with an existing ID
        mission.setId(1L);
        MissionDTO missionDTO = missionMapper.toDto(mission);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMissionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(missionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Mission in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        mission.setName(null);

        // Create the Mission, which fails.
        MissionDTO missionDTO = missionMapper.toDto(mission);

        restMissionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(missionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMissions() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        // Get all the missionList
        restMissionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mission.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getMission() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        // Get the mission
        restMissionMockMvc
            .perform(get(ENTITY_API_URL_ID, mission.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(mission.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingMission() throws Exception {
        // Get the mission
        restMissionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMission() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mission
        Mission updatedMission = missionRepository.findById(mission.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMission are not directly saved in db
        em.detach(updatedMission);
        updatedMission.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        MissionDTO missionDTO = missionMapper.toDto(updatedMission);

        restMissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, missionDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(missionDTO))
            )
            .andExpect(status().isOk());

        // Validate the Mission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMissionToMatchAllProperties(updatedMission);
    }

    @Test
    @Transactional
    void putNonExistingMission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mission.setId(longCount.incrementAndGet());

        // Create the Mission
        MissionDTO missionDTO = missionMapper.toDto(mission);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, missionDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(missionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Mission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mission.setId(longCount.incrementAndGet());

        // Create the Mission
        MissionDTO missionDTO = missionMapper.toDto(mission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(missionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Mission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mission.setId(longCount.incrementAndGet());

        // Create the Mission
        MissionDTO missionDTO = missionMapper.toDto(mission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMissionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(missionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Mission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMissionWithPatch() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mission using partial update
        Mission partialUpdatedMission = new Mission();
        partialUpdatedMission.setId(mission.getId());

        restMissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMission.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMission))
            )
            .andExpect(status().isOk());

        // Validate the Mission in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMissionUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedMission, mission), getPersistedMission(mission));
    }

    @Test
    @Transactional
    void fullUpdateMissionWithPatch() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mission using partial update
        Mission partialUpdatedMission = new Mission();
        partialUpdatedMission.setId(mission.getId());

        partialUpdatedMission.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restMissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMission.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMission))
            )
            .andExpect(status().isOk());

        // Validate the Mission in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMissionUpdatableFieldsEquals(partialUpdatedMission, getPersistedMission(partialUpdatedMission));
    }

    @Test
    @Transactional
    void patchNonExistingMission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mission.setId(longCount.incrementAndGet());

        // Create the Mission
        MissionDTO missionDTO = missionMapper.toDto(mission);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, missionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(missionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Mission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mission.setId(longCount.incrementAndGet());

        // Create the Mission
        MissionDTO missionDTO = missionMapper.toDto(mission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(missionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Mission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mission.setId(longCount.incrementAndGet());

        // Create the Mission
        MissionDTO missionDTO = missionMapper.toDto(mission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMissionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(missionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Mission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMission() throws Exception {
        // Initialize the database
        insertedMission = missionRepository.saveAndFlush(mission);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the mission
        restMissionMockMvc
            .perform(delete(ENTITY_API_URL_ID, mission.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return missionRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Mission getPersistedMission(Mission mission) {
        return missionRepository.findById(mission.getId()).orElseThrow();
    }

    protected void assertPersistedMissionToMatchAllProperties(Mission expectedMission) {
        assertMissionAllPropertiesEquals(expectedMission, getPersistedMission(expectedMission));
    }

    protected void assertPersistedMissionToMatchUpdatableProperties(Mission expectedMission) {
        assertMissionAllUpdatablePropertiesEquals(expectedMission, getPersistedMission(expectedMission));
    }
}
