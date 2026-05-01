package com.detallsublim.app.web.rest;

import static com.detallsublim.app.domain.MensajeContactoAsserts.*;
import static com.detallsublim.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.detallsublim.app.IntegrationTest;
import com.detallsublim.app.domain.MensajeContacto;
import com.detallsublim.app.repository.MensajeContactoRepository;
import com.detallsublim.app.service.dto.MensajeContactoDTO;
import com.detallsublim.app.service.mapper.MensajeContactoMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
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
 * Integration tests for the {@link MensajeContactoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MensajeContactoResourceIT {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_TELEFONO = "AAAAAAAAAA";
    private static final String UPDATED_TELEFONO = "BBBBBBBBBB";

    private static final String DEFAULT_ASUNTO = "AAAAAAAAAA";
    private static final String UPDATED_ASUNTO = "BBBBBBBBBB";

    private static final String DEFAULT_MENSAJE = "AAAAAAAAAA";
    private static final String UPDATED_MENSAJE = "BBBBBBBBBB";

    private static final Instant DEFAULT_FECHA_ENVIO = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_ENVIO = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_ATENDIDO = false;
    private static final Boolean UPDATED_ATENDIDO = true;

    private static final String ENTITY_API_URL = "/api/mensaje-contactos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MensajeContactoRepository mensajeContactoRepository;

    @Autowired
    private MensajeContactoMapper mensajeContactoMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMensajeContactoMockMvc;

    private MensajeContacto mensajeContacto;

    private MensajeContacto insertedMensajeContacto;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MensajeContacto createEntity() {
        return new MensajeContacto()
            .nombre(DEFAULT_NOMBRE)
            .email(DEFAULT_EMAIL)
            .telefono(DEFAULT_TELEFONO)
            .asunto(DEFAULT_ASUNTO)
            .mensaje(DEFAULT_MENSAJE)
            .fechaEnvio(DEFAULT_FECHA_ENVIO)
            .atendido(DEFAULT_ATENDIDO);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MensajeContacto createUpdatedEntity() {
        return new MensajeContacto()
            .nombre(UPDATED_NOMBRE)
            .email(UPDATED_EMAIL)
            .telefono(UPDATED_TELEFONO)
            .asunto(UPDATED_ASUNTO)
            .mensaje(UPDATED_MENSAJE)
            .fechaEnvio(UPDATED_FECHA_ENVIO)
            .atendido(UPDATED_ATENDIDO);
    }

    @BeforeEach
    void initTest() {
        mensajeContacto = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedMensajeContacto != null) {
            mensajeContactoRepository.delete(insertedMensajeContacto);
            insertedMensajeContacto = null;
        }
    }

    @Test
    @Transactional
    void createMensajeContacto() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the MensajeContacto
        MensajeContactoDTO mensajeContactoDTO = mensajeContactoMapper.toDto(mensajeContacto);
        var returnedMensajeContactoDTO = om.readValue(
            restMensajeContactoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mensajeContactoDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MensajeContactoDTO.class
        );

        // Validate the MensajeContacto in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMensajeContacto = mensajeContactoMapper.toEntity(returnedMensajeContactoDTO);
        assertMensajeContactoUpdatableFieldsEquals(returnedMensajeContacto, getPersistedMensajeContacto(returnedMensajeContacto));

        insertedMensajeContacto = returnedMensajeContacto;
    }

    @Test
    @Transactional
    void createMensajeContactoWithExistingId() throws Exception {
        // Create the MensajeContacto with an existing ID
        mensajeContacto.setId(1L);
        MensajeContactoDTO mensajeContactoDTO = mensajeContactoMapper.toDto(mensajeContacto);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMensajeContactoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mensajeContactoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the MensajeContacto in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNombreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        mensajeContacto.setNombre(null);

        // Create the MensajeContacto, which fails.
        MensajeContactoDTO mensajeContactoDTO = mensajeContactoMapper.toDto(mensajeContacto);

        restMensajeContactoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mensajeContactoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        mensajeContacto.setEmail(null);

        // Create the MensajeContacto, which fails.
        MensajeContactoDTO mensajeContactoDTO = mensajeContactoMapper.toDto(mensajeContacto);

        restMensajeContactoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mensajeContactoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAsuntoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        mensajeContacto.setAsunto(null);

        // Create the MensajeContacto, which fails.
        MensajeContactoDTO mensajeContactoDTO = mensajeContactoMapper.toDto(mensajeContacto);

        restMensajeContactoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mensajeContactoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFechaEnvioIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        mensajeContacto.setFechaEnvio(null);

        // Create the MensajeContacto, which fails.
        MensajeContactoDTO mensajeContactoDTO = mensajeContactoMapper.toDto(mensajeContacto);

        restMensajeContactoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mensajeContactoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAtendidoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        mensajeContacto.setAtendido(null);

        // Create the MensajeContacto, which fails.
        MensajeContactoDTO mensajeContactoDTO = mensajeContactoMapper.toDto(mensajeContacto);

        restMensajeContactoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mensajeContactoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMensajeContactos() throws Exception {
        // Initialize the database
        insertedMensajeContacto = mensajeContactoRepository.saveAndFlush(mensajeContacto);

        // Get all the mensajeContactoList
        restMensajeContactoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mensajeContacto.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].telefono").value(hasItem(DEFAULT_TELEFONO)))
            .andExpect(jsonPath("$.[*].asunto").value(hasItem(DEFAULT_ASUNTO)))
            .andExpect(jsonPath("$.[*].mensaje").value(hasItem(DEFAULT_MENSAJE)))
            .andExpect(jsonPath("$.[*].fechaEnvio").value(hasItem(DEFAULT_FECHA_ENVIO.toString())))
            .andExpect(jsonPath("$.[*].atendido").value(hasItem(DEFAULT_ATENDIDO)));
    }

    @Test
    @Transactional
    void getMensajeContacto() throws Exception {
        // Initialize the database
        insertedMensajeContacto = mensajeContactoRepository.saveAndFlush(mensajeContacto);

        // Get the mensajeContacto
        restMensajeContactoMockMvc
            .perform(get(ENTITY_API_URL_ID, mensajeContacto.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(mensajeContacto.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.telefono").value(DEFAULT_TELEFONO))
            .andExpect(jsonPath("$.asunto").value(DEFAULT_ASUNTO))
            .andExpect(jsonPath("$.mensaje").value(DEFAULT_MENSAJE))
            .andExpect(jsonPath("$.fechaEnvio").value(DEFAULT_FECHA_ENVIO.toString()))
            .andExpect(jsonPath("$.atendido").value(DEFAULT_ATENDIDO));
    }

    @Test
    @Transactional
    void getNonExistingMensajeContacto() throws Exception {
        // Get the mensajeContacto
        restMensajeContactoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMensajeContacto() throws Exception {
        // Initialize the database
        insertedMensajeContacto = mensajeContactoRepository.saveAndFlush(mensajeContacto);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mensajeContacto
        MensajeContacto updatedMensajeContacto = mensajeContactoRepository.findById(mensajeContacto.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMensajeContacto are not directly saved in db
        em.detach(updatedMensajeContacto);
        updatedMensajeContacto
            .nombre(UPDATED_NOMBRE)
            .email(UPDATED_EMAIL)
            .telefono(UPDATED_TELEFONO)
            .asunto(UPDATED_ASUNTO)
            .mensaje(UPDATED_MENSAJE)
            .fechaEnvio(UPDATED_FECHA_ENVIO)
            .atendido(UPDATED_ATENDIDO);
        MensajeContactoDTO mensajeContactoDTO = mensajeContactoMapper.toDto(updatedMensajeContacto);

        restMensajeContactoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, mensajeContactoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(mensajeContactoDTO))
            )
            .andExpect(status().isOk());

        // Validate the MensajeContacto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMensajeContactoToMatchAllProperties(updatedMensajeContacto);
    }

    @Test
    @Transactional
    void putNonExistingMensajeContacto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mensajeContacto.setId(longCount.incrementAndGet());

        // Create the MensajeContacto
        MensajeContactoDTO mensajeContactoDTO = mensajeContactoMapper.toDto(mensajeContacto);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMensajeContactoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, mensajeContactoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(mensajeContactoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MensajeContacto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMensajeContacto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mensajeContacto.setId(longCount.incrementAndGet());

        // Create the MensajeContacto
        MensajeContactoDTO mensajeContactoDTO = mensajeContactoMapper.toDto(mensajeContacto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMensajeContactoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(mensajeContactoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MensajeContacto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMensajeContacto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mensajeContacto.setId(longCount.incrementAndGet());

        // Create the MensajeContacto
        MensajeContactoDTO mensajeContactoDTO = mensajeContactoMapper.toDto(mensajeContacto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMensajeContactoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mensajeContactoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MensajeContacto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMensajeContactoWithPatch() throws Exception {
        // Initialize the database
        insertedMensajeContacto = mensajeContactoRepository.saveAndFlush(mensajeContacto);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mensajeContacto using partial update
        MensajeContacto partialUpdatedMensajeContacto = new MensajeContacto();
        partialUpdatedMensajeContacto.setId(mensajeContacto.getId());

        partialUpdatedMensajeContacto
            .email(UPDATED_EMAIL)
            .telefono(UPDATED_TELEFONO)
            .asunto(UPDATED_ASUNTO)
            .mensaje(UPDATED_MENSAJE)
            .fechaEnvio(UPDATED_FECHA_ENVIO)
            .atendido(UPDATED_ATENDIDO);

        restMensajeContactoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMensajeContacto.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMensajeContacto))
            )
            .andExpect(status().isOk());

        // Validate the MensajeContacto in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMensajeContactoUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMensajeContacto, mensajeContacto),
            getPersistedMensajeContacto(mensajeContacto)
        );
    }

    @Test
    @Transactional
    void fullUpdateMensajeContactoWithPatch() throws Exception {
        // Initialize the database
        insertedMensajeContacto = mensajeContactoRepository.saveAndFlush(mensajeContacto);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mensajeContacto using partial update
        MensajeContacto partialUpdatedMensajeContacto = new MensajeContacto();
        partialUpdatedMensajeContacto.setId(mensajeContacto.getId());

        partialUpdatedMensajeContacto
            .nombre(UPDATED_NOMBRE)
            .email(UPDATED_EMAIL)
            .telefono(UPDATED_TELEFONO)
            .asunto(UPDATED_ASUNTO)
            .mensaje(UPDATED_MENSAJE)
            .fechaEnvio(UPDATED_FECHA_ENVIO)
            .atendido(UPDATED_ATENDIDO);

        restMensajeContactoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMensajeContacto.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMensajeContacto))
            )
            .andExpect(status().isOk());

        // Validate the MensajeContacto in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMensajeContactoUpdatableFieldsEquals(
            partialUpdatedMensajeContacto,
            getPersistedMensajeContacto(partialUpdatedMensajeContacto)
        );
    }

    @Test
    @Transactional
    void patchNonExistingMensajeContacto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mensajeContacto.setId(longCount.incrementAndGet());

        // Create the MensajeContacto
        MensajeContactoDTO mensajeContactoDTO = mensajeContactoMapper.toDto(mensajeContacto);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMensajeContactoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, mensajeContactoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(mensajeContactoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MensajeContacto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMensajeContacto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mensajeContacto.setId(longCount.incrementAndGet());

        // Create the MensajeContacto
        MensajeContactoDTO mensajeContactoDTO = mensajeContactoMapper.toDto(mensajeContacto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMensajeContactoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(mensajeContactoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MensajeContacto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMensajeContacto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mensajeContacto.setId(longCount.incrementAndGet());

        // Create the MensajeContacto
        MensajeContactoDTO mensajeContactoDTO = mensajeContactoMapper.toDto(mensajeContacto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMensajeContactoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(mensajeContactoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MensajeContacto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMensajeContacto() throws Exception {
        // Initialize the database
        insertedMensajeContacto = mensajeContactoRepository.saveAndFlush(mensajeContacto);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the mensajeContacto
        restMensajeContactoMockMvc
            .perform(delete(ENTITY_API_URL_ID, mensajeContacto.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return mensajeContactoRepository.count();
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

    protected MensajeContacto getPersistedMensajeContacto(MensajeContacto mensajeContacto) {
        return mensajeContactoRepository.findById(mensajeContacto.getId()).orElseThrow();
    }

    protected void assertPersistedMensajeContactoToMatchAllProperties(MensajeContacto expectedMensajeContacto) {
        assertMensajeContactoAllPropertiesEquals(expectedMensajeContacto, getPersistedMensajeContacto(expectedMensajeContacto));
    }

    protected void assertPersistedMensajeContactoToMatchUpdatableProperties(MensajeContacto expectedMensajeContacto) {
        assertMensajeContactoAllUpdatablePropertiesEquals(expectedMensajeContacto, getPersistedMensajeContacto(expectedMensajeContacto));
    }
}
