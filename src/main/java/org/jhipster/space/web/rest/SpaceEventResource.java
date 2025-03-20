package org.jhipster.space.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.jhipster.space.repository.SpaceEventRepository;
import org.jhipster.space.service.SpaceEventService;
import org.jhipster.space.service.dto.SpaceEventDTO;
import org.jhipster.space.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link org.jhipster.space.domain.SpaceEvent}.
 */
@RestController
@RequestMapping("/api/space-events")
public class SpaceEventResource {

    private static final Logger LOG = LoggerFactory.getLogger(SpaceEventResource.class);

    private static final String ENTITY_NAME = "spaceEvent";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SpaceEventService spaceEventService;

    private final SpaceEventRepository spaceEventRepository;

    public SpaceEventResource(SpaceEventService spaceEventService, SpaceEventRepository spaceEventRepository) {
        this.spaceEventService = spaceEventService;
        this.spaceEventRepository = spaceEventRepository;
    }

    /**
     * {@code POST  /space-events} : Create a new spaceEvent.
     *
     * @param spaceEventDTO the spaceEventDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new spaceEventDTO, or with status {@code 400 (Bad Request)} if the spaceEvent has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SpaceEventDTO> createSpaceEvent(@Valid @RequestBody SpaceEventDTO spaceEventDTO) throws URISyntaxException {
        LOG.debug("REST request to save SpaceEvent : {}", spaceEventDTO);
        if (spaceEventDTO.getId() != null) {
            throw new BadRequestAlertException("A new spaceEvent cannot already have an ID", ENTITY_NAME, "idexists");
        }
        spaceEventDTO = spaceEventService.save(spaceEventDTO);
        return ResponseEntity.created(new URI("/api/space-events/" + spaceEventDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, spaceEventDTO.getId().toString()))
            .body(spaceEventDTO);
    }

    /**
     * {@code PUT  /space-events/:id} : Updates an existing spaceEvent.
     *
     * @param id the id of the spaceEventDTO to save.
     * @param spaceEventDTO the spaceEventDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated spaceEventDTO,
     * or with status {@code 400 (Bad Request)} if the spaceEventDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the spaceEventDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SpaceEventDTO> updateSpaceEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SpaceEventDTO spaceEventDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SpaceEvent : {}, {}", id, spaceEventDTO);
        if (spaceEventDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, spaceEventDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!spaceEventRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        spaceEventDTO = spaceEventService.update(spaceEventDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, spaceEventDTO.getId().toString()))
            .body(spaceEventDTO);
    }

    /**
     * {@code PATCH  /space-events/:id} : Partial updates given fields of an existing spaceEvent, field will ignore if it is null
     *
     * @param id the id of the spaceEventDTO to save.
     * @param spaceEventDTO the spaceEventDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated spaceEventDTO,
     * or with status {@code 400 (Bad Request)} if the spaceEventDTO is not valid,
     * or with status {@code 404 (Not Found)} if the spaceEventDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the spaceEventDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SpaceEventDTO> partialUpdateSpaceEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SpaceEventDTO spaceEventDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SpaceEvent partially : {}, {}", id, spaceEventDTO);
        if (spaceEventDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, spaceEventDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!spaceEventRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SpaceEventDTO> result = spaceEventService.partialUpdate(spaceEventDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, spaceEventDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /space-events} : get all the spaceEvents.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of spaceEvents in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SpaceEventDTO>> getAllSpaceEvents(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of SpaceEvents");
        Page<SpaceEventDTO> page;
        if (eagerload) {
            page = spaceEventService.findAllWithEagerRelationships(pageable);
        } else {
            page = spaceEventService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /space-events/:id} : get the "id" spaceEvent.
     *
     * @param id the id of the spaceEventDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the spaceEventDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SpaceEventDTO> getSpaceEvent(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SpaceEvent : {}", id);
        Optional<SpaceEventDTO> spaceEventDTO = spaceEventService.findOne(id);
        return ResponseUtil.wrapOrNotFound(spaceEventDTO);
    }

    /**
     * {@code DELETE  /space-events/:id} : delete the "id" spaceEvent.
     *
     * @param id the id of the spaceEventDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpaceEvent(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SpaceEvent : {}", id);
        spaceEventService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
