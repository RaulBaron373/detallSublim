package com.detallsublim.app.web.rest;

import static com.detallsublim.app.domain.SolicitudPresupuestoAsserts.*;
import static com.detallsublim.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.detallsublim.app.IntegrationTest;
import com.detallsublim.app.domain.SolicitudPresupuesto;
import com.detallsublim.app.domain.enumeration.EstadoSolicitud;
import com.detallsublim.app.repository.SolicitudPresupuestoRepository;
import com.detallsublim.app.service.SolicitudPresupuestoService;
import com.detallsublim.app.service.dto.SolicitudPresupuestoDTO;
import com.detallsublim.app.service.mapper.SolicitudPresupuestoMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
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
 * Integration tests for the {@link SolicitudPresupuestoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SolicitudPresupuestoResourceIT {

    private static final String DEFAULT_NOMBRE_CLIENTE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE_CLIENTE = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "default@example.com";
    private static final String UPDATED_EMAIL = "updated@example.com";

    private static final String DEFAULT_TELEFONO = "AAAAAAAAAA";
    private static final String UPDATED_TELEFONO = "BBBBBBBBBB";

    private static final String DEFAULT_NOMBRE_EMPRESA = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE_EMPRESA = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPCION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPCION = "BBBBBBBBBB";

    private static final Integer DEFAULT_CANTIDAD = 1;
    private static final Integer UPDATED_CANTIDAD = 2;

    private static final Instant DEFAULT_FECHA_SOLICITUD = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_SOLICITUD = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final EstadoSolicitud DEFAULT_ESTADO = EstadoSolicitud.PENDIENTE;
    private static final EstadoSolicitud UPDATED_ESTADO = EstadoSolicitud.ACEPTADO;

    private static final String DEFAULT_OBSERVACIONES_INTERNAS = "AAAAAAAAAA";
    private static final String UPDATED_OBSERVACIONES_INTERNAS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/solicitud-presupuestos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SolicitudPresupuestoRepository solicitudPresupuestoRepository;

    @Mock
    private SolicitudPresupuestoRepository solicitudPresupuestoRepositoryMock;

    @Autowired
    private SolicitudPresupuestoMapper solicitudPresupuestoMapper;

    @Mock
    private SolicitudPresupuestoService solicitudPresupuestoServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSolicitudPresupuestoMockMvc;

    private SolicitudPresupuesto solicitudPresupuesto;

    private SolicitudPresupuesto insertedSolicitudPresupuesto;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SolicitudPresupuesto createEntity() {
        return new SolicitudPresupuesto()
            .nombreCliente(DEFAULT_NOMBRE_CLIENTE)
            .email(DEFAULT_EMAIL)
            .telefono(DEFAULT_TELEFONO)
            .nombreEmpresa(DEFAULT_NOMBRE_EMPRESA)
            .descripcion(DEFAULT_DESCRIPCION)
            .cantidad(DEFAULT_CANTIDAD)
            .fechaSolicitud(DEFAULT_FECHA_SOLICITUD)
            .estado(DEFAULT_ESTADO)
            .observacionesInternas(DEFAULT_OBSERVACIONES_INTERNAS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SolicitudPresupuesto createUpdatedEntity() {
        return new SolicitudPresupuesto()
            .nombreCliente(UPDATED_NOMBRE_CLIENTE)
            .email(UPDATED_EMAIL)
            .telefono(UPDATED_TELEFONO)
            .nombreEmpresa(UPDATED_NOMBRE_EMPRESA)
            .descripcion(UPDATED_DESCRIPCION)
            .cantidad(UPDATED_CANTIDAD)
            .fechaSolicitud(UPDATED_FECHA_SOLICITUD)
            .estado(UPDATED_ESTADO)
            .observacionesInternas(UPDATED_OBSERVACIONES_INTERNAS);
    }

    @BeforeEach
    void initTest() {
        solicitudPresupuesto = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSolicitudPresupuesto != null) {
            solicitudPresupuestoRepository.delete(insertedSolicitudPresupuesto);
            insertedSolicitudPresupuesto = null;
        }
    }

    @Test
    @Transactional
    void createSolicitudPresupuesto() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SolicitudPresupuesto
        SolicitudPresupuestoDTO solicitudPresupuestoDTO = solicitudPresupuestoMapper.toDto(solicitudPresupuesto);
        var returnedSolicitudPresupuestoDTO = om.readValue(
            restSolicitudPresupuestoMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(solicitudPresupuestoDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SolicitudPresupuestoDTO.class
        );

        // Validate the SolicitudPresupuesto in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSolicitudPresupuesto = solicitudPresupuestoMapper.toEntity(returnedSolicitudPresupuestoDTO);
        assertSolicitudPresupuestoUpdatableFieldsEquals(
            returnedSolicitudPresupuesto,
            getPersistedSolicitudPresupuesto(returnedSolicitudPresupuesto)
        );

        insertedSolicitudPresupuesto = returnedSolicitudPresupuesto;
    }

    @Test
    @Transactional
    void createSolicitudPresupuestoWithExistingId() throws Exception {
        // Create the SolicitudPresupuesto with an existing ID
        solicitudPresupuesto.setId(1L);
        SolicitudPresupuestoDTO solicitudPresupuestoDTO = solicitudPresupuestoMapper.toDto(solicitudPresupuesto);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSolicitudPresupuestoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(solicitudPresupuestoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SolicitudPresupuesto in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNombreClienteIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        solicitudPresupuesto.setNombreCliente(null);

        // Create the SolicitudPresupuesto, which fails.
        SolicitudPresupuestoDTO solicitudPresupuestoDTO = solicitudPresupuestoMapper.toDto(solicitudPresupuesto);

        restSolicitudPresupuestoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(solicitudPresupuestoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        solicitudPresupuesto.setEmail(null);

        // Create the SolicitudPresupuesto, which fails.
        SolicitudPresupuestoDTO solicitudPresupuestoDTO = solicitudPresupuestoMapper.toDto(solicitudPresupuesto);

        restSolicitudPresupuestoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(solicitudPresupuestoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCantidadIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        solicitudPresupuesto.setCantidad(null);

        // Create the SolicitudPresupuesto, which fails.
        SolicitudPresupuestoDTO solicitudPresupuestoDTO = solicitudPresupuestoMapper.toDto(solicitudPresupuesto);

        restSolicitudPresupuestoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(solicitudPresupuestoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFechaSolicitudIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        solicitudPresupuesto.setFechaSolicitud(null);

        // Create the SolicitudPresupuesto, which fails.
        SolicitudPresupuestoDTO solicitudPresupuestoDTO = solicitudPresupuestoMapper.toDto(solicitudPresupuesto);

        restSolicitudPresupuestoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(solicitudPresupuestoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEstadoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        solicitudPresupuesto.setEstado(null);

        // Create the SolicitudPresupuesto, which fails.
        SolicitudPresupuestoDTO solicitudPresupuestoDTO = solicitudPresupuestoMapper.toDto(solicitudPresupuesto);

        restSolicitudPresupuestoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(solicitudPresupuestoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSolicitudPresupuestos() throws Exception {
        // Initialize the database
        insertedSolicitudPresupuesto = solicitudPresupuestoRepository.saveAndFlush(solicitudPresupuesto);

        // Get all the solicitudPresupuestoList
        restSolicitudPresupuestoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(solicitudPresupuesto.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombreCliente").value(hasItem(DEFAULT_NOMBRE_CLIENTE)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].telefono").value(hasItem(DEFAULT_TELEFONO)))
            .andExpect(jsonPath("$.[*].nombreEmpresa").value(hasItem(DEFAULT_NOMBRE_EMPRESA)))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)))
            .andExpect(jsonPath("$.[*].cantidad").value(hasItem(DEFAULT_CANTIDAD)))
            .andExpect(jsonPath("$.[*].fechaSolicitud").value(hasItem(DEFAULT_FECHA_SOLICITUD.toString())))
            .andExpect(jsonPath("$.[*].estado").value(hasItem(DEFAULT_ESTADO.toString())))
            .andExpect(jsonPath("$.[*].observacionesInternas").value(hasItem(DEFAULT_OBSERVACIONES_INTERNAS)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSolicitudPresupuestosWithEagerRelationshipsIsEnabled() throws Exception {
        when(solicitudPresupuestoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSolicitudPresupuestoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(solicitudPresupuestoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSolicitudPresupuestosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(solicitudPresupuestoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSolicitudPresupuestoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(solicitudPresupuestoRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSolicitudPresupuesto() throws Exception {
        // Initialize the database
        insertedSolicitudPresupuesto = solicitudPresupuestoRepository.saveAndFlush(solicitudPresupuesto);

        // Get the solicitudPresupuesto
        restSolicitudPresupuestoMockMvc
            .perform(get(ENTITY_API_URL_ID, solicitudPresupuesto.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(solicitudPresupuesto.getId().intValue()))
            .andExpect(jsonPath("$.nombreCliente").value(DEFAULT_NOMBRE_CLIENTE))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.telefono").value(DEFAULT_TELEFONO))
            .andExpect(jsonPath("$.nombreEmpresa").value(DEFAULT_NOMBRE_EMPRESA))
            .andExpect(jsonPath("$.descripcion").value(DEFAULT_DESCRIPCION))
            .andExpect(jsonPath("$.cantidad").value(DEFAULT_CANTIDAD))
            .andExpect(jsonPath("$.fechaSolicitud").value(DEFAULT_FECHA_SOLICITUD.toString()))
            .andExpect(jsonPath("$.estado").value(DEFAULT_ESTADO.toString()))
            .andExpect(jsonPath("$.observacionesInternas").value(DEFAULT_OBSERVACIONES_INTERNAS));
    }

    @Test
    @Transactional
    void getNonExistingSolicitudPresupuesto() throws Exception {
        // Get the solicitudPresupuesto
        restSolicitudPresupuestoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSolicitudPresupuesto() throws Exception {
        // Initialize the database
        insertedSolicitudPresupuesto = solicitudPresupuestoRepository.saveAndFlush(solicitudPresupuesto);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the solicitudPresupuesto
        SolicitudPresupuesto updatedSolicitudPresupuesto = solicitudPresupuestoRepository
            .findById(solicitudPresupuesto.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedSolicitudPresupuesto are not directly saved in db
        em.detach(updatedSolicitudPresupuesto);
        updatedSolicitudPresupuesto
            .nombreCliente(UPDATED_NOMBRE_CLIENTE)
            .email(UPDATED_EMAIL)
            .telefono(UPDATED_TELEFONO)
            .nombreEmpresa(UPDATED_NOMBRE_EMPRESA)
            .descripcion(UPDATED_DESCRIPCION)
            .cantidad(UPDATED_CANTIDAD)
            .fechaSolicitud(UPDATED_FECHA_SOLICITUD)
            .estado(UPDATED_ESTADO)
            .observacionesInternas(UPDATED_OBSERVACIONES_INTERNAS);
        SolicitudPresupuestoDTO solicitudPresupuestoDTO = solicitudPresupuestoMapper.toDto(updatedSolicitudPresupuesto);

        restSolicitudPresupuestoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, solicitudPresupuestoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(solicitudPresupuestoDTO))
            )
            .andExpect(status().isOk());

        // Validate the SolicitudPresupuesto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSolicitudPresupuestoToMatchAllProperties(updatedSolicitudPresupuesto);
    }

    @Test
    @Transactional
    void putNonExistingSolicitudPresupuesto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solicitudPresupuesto.setId(longCount.incrementAndGet());

        // Create the SolicitudPresupuesto
        SolicitudPresupuestoDTO solicitudPresupuestoDTO = solicitudPresupuestoMapper.toDto(solicitudPresupuesto);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSolicitudPresupuestoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, solicitudPresupuestoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(solicitudPresupuestoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SolicitudPresupuesto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSolicitudPresupuesto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solicitudPresupuesto.setId(longCount.incrementAndGet());

        // Create the SolicitudPresupuesto
        SolicitudPresupuestoDTO solicitudPresupuestoDTO = solicitudPresupuestoMapper.toDto(solicitudPresupuesto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSolicitudPresupuestoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(solicitudPresupuestoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SolicitudPresupuesto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSolicitudPresupuesto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solicitudPresupuesto.setId(longCount.incrementAndGet());

        // Create the SolicitudPresupuesto
        SolicitudPresupuestoDTO solicitudPresupuestoDTO = solicitudPresupuestoMapper.toDto(solicitudPresupuesto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSolicitudPresupuestoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(solicitudPresupuestoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SolicitudPresupuesto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSolicitudPresupuestoWithPatch() throws Exception {
        // Initialize the database
        insertedSolicitudPresupuesto = solicitudPresupuestoRepository.saveAndFlush(solicitudPresupuesto);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the solicitudPresupuesto using partial update
        SolicitudPresupuesto partialUpdatedSolicitudPresupuesto = new SolicitudPresupuesto();
        partialUpdatedSolicitudPresupuesto.setId(solicitudPresupuesto.getId());

        partialUpdatedSolicitudPresupuesto
            .nombreCliente(UPDATED_NOMBRE_CLIENTE)
            .email(UPDATED_EMAIL)
            .telefono(UPDATED_TELEFONO)
            .nombreEmpresa(UPDATED_NOMBRE_EMPRESA)
            .cantidad(UPDATED_CANTIDAD);

        restSolicitudPresupuestoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSolicitudPresupuesto.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSolicitudPresupuesto))
            )
            .andExpect(status().isOk());

        // Validate the SolicitudPresupuesto in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSolicitudPresupuestoUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSolicitudPresupuesto, solicitudPresupuesto),
            getPersistedSolicitudPresupuesto(solicitudPresupuesto)
        );
    }

    @Test
    @Transactional
    void fullUpdateSolicitudPresupuestoWithPatch() throws Exception {
        // Initialize the database
        insertedSolicitudPresupuesto = solicitudPresupuestoRepository.saveAndFlush(solicitudPresupuesto);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the solicitudPresupuesto using partial update
        SolicitudPresupuesto partialUpdatedSolicitudPresupuesto = new SolicitudPresupuesto();
        partialUpdatedSolicitudPresupuesto.setId(solicitudPresupuesto.getId());

        partialUpdatedSolicitudPresupuesto
            .nombreCliente(UPDATED_NOMBRE_CLIENTE)
            .email(UPDATED_EMAIL)
            .telefono(UPDATED_TELEFONO)
            .nombreEmpresa(UPDATED_NOMBRE_EMPRESA)
            .descripcion(UPDATED_DESCRIPCION)
            .cantidad(UPDATED_CANTIDAD)
            .fechaSolicitud(UPDATED_FECHA_SOLICITUD)
            .estado(UPDATED_ESTADO)
            .observacionesInternas(UPDATED_OBSERVACIONES_INTERNAS);

        restSolicitudPresupuestoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSolicitudPresupuesto.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSolicitudPresupuesto))
            )
            .andExpect(status().isOk());

        // Validate the SolicitudPresupuesto in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSolicitudPresupuestoUpdatableFieldsEquals(
            partialUpdatedSolicitudPresupuesto,
            getPersistedSolicitudPresupuesto(partialUpdatedSolicitudPresupuesto)
        );
    }

    @Test
    @Transactional
    void patchNonExistingSolicitudPresupuesto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solicitudPresupuesto.setId(longCount.incrementAndGet());

        // Create the SolicitudPresupuesto
        SolicitudPresupuestoDTO solicitudPresupuestoDTO = solicitudPresupuestoMapper.toDto(solicitudPresupuesto);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSolicitudPresupuestoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, solicitudPresupuestoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(solicitudPresupuestoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SolicitudPresupuesto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSolicitudPresupuesto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solicitudPresupuesto.setId(longCount.incrementAndGet());

        // Create the SolicitudPresupuesto
        SolicitudPresupuestoDTO solicitudPresupuestoDTO = solicitudPresupuestoMapper.toDto(solicitudPresupuesto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSolicitudPresupuestoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(solicitudPresupuestoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SolicitudPresupuesto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSolicitudPresupuesto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solicitudPresupuesto.setId(longCount.incrementAndGet());

        // Create the SolicitudPresupuesto
        SolicitudPresupuestoDTO solicitudPresupuestoDTO = solicitudPresupuestoMapper.toDto(solicitudPresupuesto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSolicitudPresupuestoMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(solicitudPresupuestoDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SolicitudPresupuesto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSolicitudPresupuesto() throws Exception {
        // Initialize the database
        insertedSolicitudPresupuesto = solicitudPresupuestoRepository.saveAndFlush(solicitudPresupuesto);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the solicitudPresupuesto
        restSolicitudPresupuestoMockMvc
            .perform(delete(ENTITY_API_URL_ID, solicitudPresupuesto.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return solicitudPresupuestoRepository.count();
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

    protected SolicitudPresupuesto getPersistedSolicitudPresupuesto(SolicitudPresupuesto solicitudPresupuesto) {
        return solicitudPresupuestoRepository.findById(solicitudPresupuesto.getId()).orElseThrow();
    }

    protected void assertPersistedSolicitudPresupuestoToMatchAllProperties(SolicitudPresupuesto expectedSolicitudPresupuesto) {
        assertSolicitudPresupuestoAllPropertiesEquals(
            expectedSolicitudPresupuesto,
            getPersistedSolicitudPresupuesto(expectedSolicitudPresupuesto)
        );
    }

    protected void assertPersistedSolicitudPresupuestoToMatchUpdatableProperties(SolicitudPresupuesto expectedSolicitudPresupuesto) {
        assertSolicitudPresupuestoAllUpdatablePropertiesEquals(
            expectedSolicitudPresupuesto,
            getPersistedSolicitudPresupuesto(expectedSolicitudPresupuesto)
        );
    }
}
