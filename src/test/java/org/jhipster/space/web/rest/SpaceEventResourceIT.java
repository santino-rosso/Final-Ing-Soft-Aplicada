package org.jhipster.space.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.jhipster.space.domain.SpaceEventAsserts.*;
import static org.jhipster.space.web.rest.TestUtil.createUpdateProxyForBean;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.jhipster.space.IntegrationTest;
import org.jhipster.space.domain.SpaceEvent;
import org.jhipster.space.domain.enumeration.SpaceEventType;
import org.jhipster.space.repository.SpaceEventRepository;
import org.jhipster.space.service.SpaceEventService;
import org.jhipster.space.service.dto.SpaceEventDTO;
import org.jhipster.space.service.mapper.SpaceEventMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SpaceEventResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SpaceEventResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final byte[] DEFAULT_PHOTO = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PHOTO = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_PHOTO_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PHOTO_CONTENT_TYPE = "image/png";

    private static final SpaceEventType DEFAULT_TYPE = SpaceEventType.LAUNCH;
    private static final SpaceEventType UPDATED_TYPE = SpaceEventType.LANDING;

    private static final String ENTITY_API_URL = "/api/space-events";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SpaceEventRepository spaceEventRepository;

    @Mock
    private SpaceEventRepository spaceEventRepositoryMock;

    @Autowired
    private SpaceEventMapper spaceEventMapper;

    @Mock
    private SpaceEventService spaceEventServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSpaceEventMockMvc;

    private SpaceEvent spaceEvent;

    private SpaceEvent insertedSpaceEvent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SpaceEvent createEntity() {
        return new SpaceEvent()
            .name(DEFAULT_NAME)
            .date(DEFAULT_DATE)
            .description(DEFAULT_DESCRIPTION)
            .photo(DEFAULT_PHOTO)
            .photoContentType(DEFAULT_PHOTO_CONTENT_TYPE)
            .type(DEFAULT_TYPE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SpaceEvent createUpdatedEntity() {
        return new SpaceEvent()
            .name(UPDATED_NAME)
            .date(UPDATED_DATE)
            .description(UPDATED_DESCRIPTION)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .type(UPDATED_TYPE);
    }

    @BeforeEach
    public void initTest() {
        spaceEvent = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedSpaceEvent != null) {
            spaceEventRepository.delete(insertedSpaceEvent);
            insertedSpaceEvent = null;
        }
    }

    @Test
    @Transactional
    void createSpaceEvent() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SpaceEvent
        SpaceEventDTO spaceEventDTO = spaceEventMapper.toDto(spaceEvent);
        var returnedSpaceEventDTO = om.readValue(
            restSpaceEventMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(spaceEventDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SpaceEventDTO.class
        );

        // Validate the SpaceEvent in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSpaceEvent = spaceEventMapper.toEntity(returnedSpaceEventDTO);
        assertSpaceEventUpdatableFieldsEquals(returnedSpaceEvent, getPersistedSpaceEvent(returnedSpaceEvent));

        insertedSpaceEvent = returnedSpaceEvent;
    }

    @Test
    @Transactional
    void createSpaceEventWithExistingId() throws Exception {
        // Create the SpaceEvent with an existing ID
        spaceEvent.setId(1L);
        SpaceEventDTO spaceEventDTO = spaceEventMapper.toDto(spaceEvent);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSpaceEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(spaceEventDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SpaceEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        spaceEvent.setName(null);

        // Create the SpaceEvent, which fails.
        SpaceEventDTO spaceEventDTO = spaceEventMapper.toDto(spaceEvent);

        restSpaceEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(spaceEventDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        spaceEvent.setDate(null);

        // Create the SpaceEvent, which fails.
        SpaceEventDTO spaceEventDTO = spaceEventMapper.toDto(spaceEvent);

        restSpaceEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(spaceEventDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        spaceEvent.setType(null);

        // Create the SpaceEvent, which fails.
        SpaceEventDTO spaceEventDTO = spaceEventMapper.toDto(spaceEvent);

        restSpaceEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(spaceEventDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSpaceEvents() throws Exception {
        // Initialize the database
        insertedSpaceEvent = spaceEventRepository.saveAndFlush(spaceEvent);

        // Get all the spaceEventList
        restSpaceEventMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(spaceEvent.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].photoContentType").value(hasItem(DEFAULT_PHOTO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].photo").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_PHOTO))))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSpaceEventsWithEagerRelationshipsIsEnabled() throws Exception {
        when(spaceEventServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSpaceEventMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(spaceEventServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSpaceEventsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(spaceEventServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSpaceEventMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(spaceEventRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSpaceEvent() throws Exception {
        // Initialize the database
        insertedSpaceEvent = spaceEventRepository.saveAndFlush(spaceEvent);

        // Get the spaceEvent
        restSpaceEventMockMvc
            .perform(get(ENTITY_API_URL_ID, spaceEvent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(spaceEvent.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.photoContentType").value(DEFAULT_PHOTO_CONTENT_TYPE))
            .andExpect(jsonPath("$.photo").value(Base64.getEncoder().encodeToString(DEFAULT_PHOTO)))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingSpaceEvent() throws Exception {
        // Get the spaceEvent
        restSpaceEventMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSpaceEvent() throws Exception {
        // Initialize the database
        insertedSpaceEvent = spaceEventRepository.saveAndFlush(spaceEvent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the spaceEvent
        SpaceEvent updatedSpaceEvent = spaceEventRepository.findById(spaceEvent.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSpaceEvent are not directly saved in db
        em.detach(updatedSpaceEvent);
        updatedSpaceEvent
            .name(UPDATED_NAME)
            .date(UPDATED_DATE)
            .description(UPDATED_DESCRIPTION)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .type(UPDATED_TYPE);
        SpaceEventDTO spaceEventDTO = spaceEventMapper.toDto(updatedSpaceEvent);

        restSpaceEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, spaceEventDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(spaceEventDTO))
            )
            .andExpect(status().isOk());

        // Validate the SpaceEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSpaceEventToMatchAllProperties(updatedSpaceEvent);
    }

    @Test
    @Transactional
    void putNonExistingSpaceEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        spaceEvent.setId(longCount.incrementAndGet());

        // Create the SpaceEvent
        SpaceEventDTO spaceEventDTO = spaceEventMapper.toDto(spaceEvent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSpaceEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, spaceEventDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(spaceEventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SpaceEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSpaceEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        spaceEvent.setId(longCount.incrementAndGet());

        // Create the SpaceEvent
        SpaceEventDTO spaceEventDTO = spaceEventMapper.toDto(spaceEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpaceEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(spaceEventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SpaceEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSpaceEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        spaceEvent.setId(longCount.incrementAndGet());

        // Create the SpaceEvent
        SpaceEventDTO spaceEventDTO = spaceEventMapper.toDto(spaceEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpaceEventMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(spaceEventDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SpaceEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSpaceEventWithPatch() throws Exception {
        // Initialize the database
        insertedSpaceEvent = spaceEventRepository.saveAndFlush(spaceEvent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the spaceEvent using partial update
        SpaceEvent partialUpdatedSpaceEvent = new SpaceEvent();
        partialUpdatedSpaceEvent.setId(spaceEvent.getId());

        partialUpdatedSpaceEvent.name(UPDATED_NAME).date(UPDATED_DATE).description(UPDATED_DESCRIPTION);

        restSpaceEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSpaceEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSpaceEvent))
            )
            .andExpect(status().isOk());

        // Validate the SpaceEvent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSpaceEventUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSpaceEvent, spaceEvent),
            getPersistedSpaceEvent(spaceEvent)
        );
    }

    @Test
    @Transactional
    void fullUpdateSpaceEventWithPatch() throws Exception {
        // Initialize the database
        insertedSpaceEvent = spaceEventRepository.saveAndFlush(spaceEvent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the spaceEvent using partial update
        SpaceEvent partialUpdatedSpaceEvent = new SpaceEvent();
        partialUpdatedSpaceEvent.setId(spaceEvent.getId());

        partialUpdatedSpaceEvent
            .name(UPDATED_NAME)
            .date(UPDATED_DATE)
            .description(UPDATED_DESCRIPTION)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .type(UPDATED_TYPE);

        restSpaceEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSpaceEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSpaceEvent))
            )
            .andExpect(status().isOk());

        // Validate the SpaceEvent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSpaceEventUpdatableFieldsEquals(partialUpdatedSpaceEvent, getPersistedSpaceEvent(partialUpdatedSpaceEvent));
    }

    @Test
    @Transactional
    void patchNonExistingSpaceEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        spaceEvent.setId(longCount.incrementAndGet());

        // Create the SpaceEvent
        SpaceEventDTO spaceEventDTO = spaceEventMapper.toDto(spaceEvent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSpaceEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, spaceEventDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(spaceEventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SpaceEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSpaceEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        spaceEvent.setId(longCount.incrementAndGet());

        // Create the SpaceEvent
        SpaceEventDTO spaceEventDTO = spaceEventMapper.toDto(spaceEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpaceEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(spaceEventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SpaceEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSpaceEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        spaceEvent.setId(longCount.incrementAndGet());

        // Create the SpaceEvent
        SpaceEventDTO spaceEventDTO = spaceEventMapper.toDto(spaceEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpaceEventMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(spaceEventDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SpaceEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSpaceEvent() throws Exception {
        // Initialize the database
        insertedSpaceEvent = spaceEventRepository.saveAndFlush(spaceEvent);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the spaceEvent
        restSpaceEventMockMvc
            .perform(delete(ENTITY_API_URL_ID, spaceEvent.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return spaceEventRepository.count();
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

    protected SpaceEvent getPersistedSpaceEvent(SpaceEvent spaceEvent) {
        return spaceEventRepository.findById(spaceEvent.getId()).orElseThrow();
    }

    protected void assertPersistedSpaceEventToMatchAllProperties(SpaceEvent expectedSpaceEvent) {
        assertSpaceEventAllPropertiesEquals(expectedSpaceEvent, getPersistedSpaceEvent(expectedSpaceEvent));
    }

    protected void assertPersistedSpaceEventToMatchUpdatableProperties(SpaceEvent expectedSpaceEvent) {
        assertSpaceEventAllUpdatablePropertiesEquals(expectedSpaceEvent, getPersistedSpaceEvent(expectedSpaceEvent));
    }
}
