package com.detallsublim.app.web.rest;

import com.detallsublim.app.repository.MensajeContactoRepository;
import com.detallsublim.app.service.MensajeContactoService;
import com.detallsublim.app.service.dto.MensajeContactoDTO;
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
 * REST controller for managing {@link com.detallsublim.app.domain.MensajeContacto}.
 */
@RestController
@RequestMapping("/api/mensaje-contactos")
public class MensajeContactoResource {

    private static final Logger LOG = LoggerFactory.getLogger(MensajeContactoResource.class);

    private static final String ENTITY_NAME = "mensajeContacto";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MensajeContactoService mensajeContactoService;

    private final MensajeContactoRepository mensajeContactoRepository;

    public MensajeContactoResource(MensajeContactoService mensajeContactoService, MensajeContactoRepository mensajeContactoRepository) {
        this.mensajeContactoService = mensajeContactoService;
        this.mensajeContactoRepository = mensajeContactoRepository;
    }

    /**
     * {@code POST  /mensaje-contactos} : Create a new mensajeContacto.
     *
     * @param mensajeContactoDTO the mensajeContactoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mensajeContactoDTO, or with status {@code 400 (Bad Request)} if the mensajeContacto has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MensajeContactoDTO> createMensajeContacto(@Valid @RequestBody MensajeContactoDTO mensajeContactoDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save MensajeContacto");
        if (mensajeContactoDTO.getId() != null) {
            throw new BadRequestAlertException("A new mensajeContacto cannot already have an ID", ENTITY_NAME, "idexists");
        }
        mensajeContactoDTO = mensajeContactoService.save(mensajeContactoDTO);
        return ResponseEntity.created(new URI("/api/mensaje-contactos/" + mensajeContactoDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, mensajeContactoDTO.getId().toString()))
            .body(mensajeContactoDTO);
    }

    /**
     * {@code PUT  /mensaje-contactos/:id} : Updates an existing mensajeContacto.
     *
     * @param id the id of the mensajeContactoDTO to save.
     * @param mensajeContactoDTO the mensajeContactoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mensajeContactoDTO,
     * or with status {@code 400 (Bad Request)} if the mensajeContactoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mensajeContactoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MensajeContactoDTO> updateMensajeContacto(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MensajeContactoDTO mensajeContactoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MensajeContacto with id {}", id);
        if (mensajeContactoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mensajeContactoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!mensajeContactoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        mensajeContactoDTO = mensajeContactoService.update(mensajeContactoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, mensajeContactoDTO.getId().toString()))
            .body(mensajeContactoDTO);
    }

    /**
     * {@code PATCH  /mensaje-contactos/:id} : Partial updates given fields of an existing mensajeContacto, field will ignore if it is null
     *
     * @param id the id of the mensajeContactoDTO to save.
     * @param mensajeContactoDTO the mensajeContactoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mensajeContactoDTO,
     * or with status {@code 400 (Bad Request)} if the mensajeContactoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the mensajeContactoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the mensajeContactoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MensajeContactoDTO> partialUpdateMensajeContacto(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MensajeContactoDTO mensajeContactoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update MensajeContacto with id {}", id);
        if (mensajeContactoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mensajeContactoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!mensajeContactoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MensajeContactoDTO> result = mensajeContactoService.partialUpdate(mensajeContactoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, mensajeContactoDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /mensaje-contactos} : get all the mensajeContactos.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of mensajeContactos in body.
     */
    @GetMapping("")
    public ResponseEntity<List<MensajeContactoDTO>> getAllMensajeContactos(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of MensajeContactos");
        Page<MensajeContactoDTO> page = mensajeContactoService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /mensaje-contactos/:id} : get the "id" mensajeContacto.
     *
     * @param id the id of the mensajeContactoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mensajeContactoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MensajeContactoDTO> getMensajeContacto(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MensajeContacto : {}", id);
        Optional<MensajeContactoDTO> mensajeContactoDTO = mensajeContactoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(mensajeContactoDTO);
    }

    /**
     * {@code DELETE  /mensaje-contactos/:id} : delete the "id" mensajeContacto.
     *
     * @param id the id of the mensajeContactoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMensajeContacto(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MensajeContacto : {}", id);
        mensajeContactoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @PostMapping("/{id}/responder")
    public ResponseEntity<Void> responderMensaje(@PathVariable Long id, @RequestBody String respuesta) {
        mensajeContactoService.responderMensaje(id, respuesta);
        return ResponseEntity.ok().build();
    }
}
