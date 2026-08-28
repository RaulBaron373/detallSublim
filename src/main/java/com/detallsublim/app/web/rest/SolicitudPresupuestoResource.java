package com.detallsublim.app.web.rest;

import com.detallsublim.app.repository.SolicitudPresupuestoRepository;
import com.detallsublim.app.service.SolicitudPresupuestoService;
import com.detallsublim.app.service.dto.SolicitudPresupuestoDTO;
import com.detallsublim.app.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
 * REST controller for managing {@link com.detallsublim.app.domain.SolicitudPresupuesto}.
 */
@RestController
@RequestMapping("/api/solicitud-presupuestos")
public class SolicitudPresupuestoResource {

    private static final Logger LOG = LoggerFactory.getLogger(SolicitudPresupuestoResource.class);

    private static final String ENTITY_NAME = "solicitudPresupuesto";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SolicitudPresupuestoService solicitudPresupuestoService;

    private final SolicitudPresupuestoRepository solicitudPresupuestoRepository;

    public SolicitudPresupuestoResource(
        SolicitudPresupuestoService solicitudPresupuestoService,
        SolicitudPresupuestoRepository solicitudPresupuestoRepository
    ) {
        this.solicitudPresupuestoService = solicitudPresupuestoService;
        this.solicitudPresupuestoRepository = solicitudPresupuestoRepository;
    }

    /**
     * {@code POST  /solicitud-presupuestos} : Create a new solicitudPresupuesto.
     *
     * @param solicitudPresupuestoDTO the solicitudPresupuestoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new solicitudPresupuestoDTO, or with status {@code 400 (Bad Request)} if the solicitudPresupuesto has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SolicitudPresupuestoDTO> createSolicitudPresupuesto(
        @Valid @RequestBody SolicitudPresupuestoDTO solicitudPresupuestoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save SolicitudPresupuesto");
        if (solicitudPresupuestoDTO.getId() != null) {
            throw new BadRequestAlertException("A new solicitudPresupuesto cannot already have an ID", ENTITY_NAME, "idexists");
        }
        solicitudPresupuestoDTO = solicitudPresupuestoService.save(solicitudPresupuestoDTO);
        return ResponseEntity.created(new URI("/api/solicitud-presupuestos/" + solicitudPresupuestoDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, solicitudPresupuestoDTO.getId().toString()))
            .body(solicitudPresupuestoDTO);
    }

    /**
     * {@code PUT  /solicitud-presupuestos/:id} : Updates an existing solicitudPresupuesto.
     *
     * @param id the id of the solicitudPresupuestoDTO to save.
     * @param solicitudPresupuestoDTO the solicitudPresupuestoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated solicitudPresupuestoDTO,
     * or with status {@code 400 (Bad Request)} if the solicitudPresupuestoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the solicitudPresupuestoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SolicitudPresupuestoDTO> updateSolicitudPresupuesto(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SolicitudPresupuestoDTO solicitudPresupuestoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SolicitudPresupuesto with id {}", id);
        if (solicitudPresupuestoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, solicitudPresupuestoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!solicitudPresupuestoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        solicitudPresupuestoDTO = solicitudPresupuestoService.update(solicitudPresupuestoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, solicitudPresupuestoDTO.getId().toString()))
            .body(solicitudPresupuestoDTO);
    }

    /**
     * {@code PATCH  /solicitud-presupuestos/:id} : Partial updates given fields of an existing solicitudPresupuesto, field will ignore if it is null
     *
     * @param id the id of the solicitudPresupuestoDTO to save.
     * @param solicitudPresupuestoDTO the solicitudPresupuestoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated solicitudPresupuestoDTO,
     * or with status {@code 400 (Bad Request)} if the solicitudPresupuestoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the solicitudPresupuestoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the solicitudPresupuestoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SolicitudPresupuestoDTO> partialUpdateSolicitudPresupuesto(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SolicitudPresupuestoDTO solicitudPresupuestoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update SolicitudPresupuesto with id {}", id);
        if (solicitudPresupuestoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, solicitudPresupuestoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!solicitudPresupuestoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SolicitudPresupuestoDTO> result = solicitudPresupuestoService.partialUpdate(solicitudPresupuestoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, solicitudPresupuestoDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /solicitud-presupuestos} : get all the solicitudPresupuestos.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of solicitudPresupuestos in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SolicitudPresupuestoDTO>> getAllSolicitudPresupuestos(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of SolicitudPresupuestos");
        Page<SolicitudPresupuestoDTO> page;
        if (eagerload) {
            page = solicitudPresupuestoService.findAllWithEagerRelationships(pageable);
        } else {
            page = solicitudPresupuestoService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /solicitud-presupuestos/:id} : get the "id" solicitudPresupuesto.
     *
     * @param id the id of the solicitudPresupuestoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the solicitudPresupuestoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SolicitudPresupuestoDTO> getSolicitudPresupuesto(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SolicitudPresupuesto : {}", id);
        Optional<SolicitudPresupuestoDTO> solicitudPresupuestoDTO = solicitudPresupuestoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(solicitudPresupuestoDTO);
    }

    /**
     * {@code DELETE  /solicitud-presupuestos/:id} : delete the "id" solicitudPresupuesto.
     *
     * @param id the id of the solicitudPresupuestoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSolicitudPresupuesto(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SolicitudPresupuesto : {}", id);
        solicitudPresupuestoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
