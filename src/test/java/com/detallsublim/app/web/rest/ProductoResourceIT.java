package com.detallsublim.app.web.rest;

import static com.detallsublim.app.domain.ProductoAsserts.*;
import static com.detallsublim.app.web.rest.TestUtil.createUpdateProxyForBean;
import static com.detallsublim.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.detallsublim.app.IntegrationTest;
import com.detallsublim.app.domain.Producto;
import com.detallsublim.app.repository.ProductoRepository;
import com.detallsublim.app.service.ProductoService;
import com.detallsublim.app.service.dto.ProductoDTO;
import com.detallsublim.app.service.mapper.ProductoMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link ProductoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ProductoResourceIT {

    private static final Boolean DEFAULT_DESTACADO = false;
    private static final Boolean UPDATED_DESTACADO = true;

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPCION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPCION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRECIO_BASE = new BigDecimal(0);
    private static final BigDecimal UPDATED_PRECIO_BASE = new BigDecimal(1);

    private static final Boolean DEFAULT_PERSONALIZABLE = false;
    private static final Boolean UPDATED_PERSONALIZABLE = true;

    private static final Integer DEFAULT_PLAZO_ESTIMADO_DIAS = 1;
    private static final Integer UPDATED_PLAZO_ESTIMADO_DIAS = 2;

    private static final String DEFAULT_IMAGEN_URL =
        "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=";
    private static final String UPDATED_IMAGEN_URL =
        "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=";

    private static final Boolean DEFAULT_ACTIVO = false;
    private static final Boolean UPDATED_ACTIVO = true;

    private static final String ENTITY_API_URL = "/api/productos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProductoRepository productoRepository;

    @Mock
    private ProductoRepository productoRepositoryMock;

    @Autowired
    private ProductoMapper productoMapper;

    @Mock
    private ProductoService productoServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductoMockMvc;

    private Producto producto;

    private Producto insertedProducto;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Producto createEntity() {
        return new Producto()
            .nombre(DEFAULT_NOMBRE)
            .descripcion(DEFAULT_DESCRIPCION)
            .precioBase(DEFAULT_PRECIO_BASE)
            .personalizable(DEFAULT_PERSONALIZABLE)
            .plazoEstimadoDias(DEFAULT_PLAZO_ESTIMADO_DIAS)
            .imagenUrl(DEFAULT_IMAGEN_URL)
            .activo(DEFAULT_ACTIVO)
            .destacado(DEFAULT_DESTACADO);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Producto createUpdatedEntity() {
        return new Producto()
            .nombre(UPDATED_NOMBRE)
            .descripcion(UPDATED_DESCRIPCION)
            .precioBase(UPDATED_PRECIO_BASE)
            .personalizable(UPDATED_PERSONALIZABLE)
            .plazoEstimadoDias(UPDATED_PLAZO_ESTIMADO_DIAS)
            .imagenUrl(UPDATED_IMAGEN_URL)
            .activo(UPDATED_ACTIVO)
            .destacado(UPDATED_DESTACADO);
    }

    @BeforeEach
    void initTest() {
        producto = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedProducto != null) {
            productoRepository.delete(insertedProducto);
            insertedProducto = null;
        }
    }

    @Test
    @Transactional
    void createProducto() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Producto
        ProductoDTO productoDTO = productoMapper.toDto(producto);
        var returnedProductoDTO = om.readValue(
            restProductoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productoDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProductoDTO.class
        );

        // Validate the Producto in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProducto = productoMapper.toEntity(returnedProductoDTO);
        assertProductoUpdatableFieldsEquals(returnedProducto, getPersistedProducto(returnedProducto));

        insertedProducto = returnedProducto;
    }

    @Test
    @Transactional
    void createProductoWithExistingId() throws Exception {
        // Create the Producto with an existing ID
        producto.setId(1L);
        ProductoDTO productoDTO = productoMapper.toDto(producto);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Producto in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNombreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        producto.setNombre(null);

        // Create the Producto, which fails.
        ProductoDTO productoDTO = productoMapper.toDto(producto);

        restProductoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPrecioBaseIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        producto.setPrecioBase(null);

        // Create the Producto, which fails.
        ProductoDTO productoDTO = productoMapper.toDto(producto);

        restProductoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPersonalizableIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        producto.setPersonalizable(null);

        // Create the Producto, which fails.
        ProductoDTO productoDTO = productoMapper.toDto(producto);

        restProductoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActivoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        producto.setActivo(null);

        // Create the Producto, which fails.
        ProductoDTO productoDTO = productoMapper.toDto(producto);

        restProductoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProductos() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.saveAndFlush(producto);

        // Get all the productoList
        restProductoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(producto.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE)))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)))
            .andExpect(jsonPath("$.[*].precioBase").value(hasItem(sameNumber(DEFAULT_PRECIO_BASE))))
            .andExpect(jsonPath("$.[*].personalizable").value(hasItem(DEFAULT_PERSONALIZABLE)))
            .andExpect(jsonPath("$.[*].plazoEstimadoDias").value(hasItem(DEFAULT_PLAZO_ESTIMADO_DIAS)))
            .andExpect(jsonPath("$.[*].imagenUrl").value(hasItem(DEFAULT_IMAGEN_URL)))
            .andExpect(jsonPath("$.[*].activo").value(hasItem(DEFAULT_ACTIVO)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductosWithEagerRelationshipsIsEnabled() throws Exception {
        when(productoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProductoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(productoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(productoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProductoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(productoRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getProducto() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.saveAndFlush(producto);

        // Get the producto
        restProductoMockMvc
            .perform(get(ENTITY_API_URL_ID, producto.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(producto.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE))
            .andExpect(jsonPath("$.descripcion").value(DEFAULT_DESCRIPCION))
            .andExpect(jsonPath("$.precioBase").value(sameNumber(DEFAULT_PRECIO_BASE)))
            .andExpect(jsonPath("$.personalizable").value(DEFAULT_PERSONALIZABLE))
            .andExpect(jsonPath("$.plazoEstimadoDias").value(DEFAULT_PLAZO_ESTIMADO_DIAS))
            .andExpect(jsonPath("$.imagenUrl").value(DEFAULT_IMAGEN_URL))
            .andExpect(jsonPath("$.activo").value(DEFAULT_ACTIVO));
    }

    @Test
    @Transactional
    void getNonExistingProducto() throws Exception {
        // Get the producto
        restProductoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProducto() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.saveAndFlush(producto);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the producto
        Producto updatedProducto = productoRepository.findById(producto.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProducto are not directly saved in db
        em.detach(updatedProducto);
        updatedProducto
            .nombre(UPDATED_NOMBRE)
            .descripcion(UPDATED_DESCRIPCION)
            .precioBase(UPDATED_PRECIO_BASE)
            .personalizable(UPDATED_PERSONALIZABLE)
            .plazoEstimadoDias(UPDATED_PLAZO_ESTIMADO_DIAS)
            .imagenUrl(UPDATED_IMAGEN_URL)
            .activo(UPDATED_ACTIVO);
        ProductoDTO productoDTO = productoMapper.toDto(updatedProducto);

        restProductoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productoDTO))
            )
            .andExpect(status().isOk());

        // Validate the Producto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProductoToMatchAllProperties(updatedProducto);
    }

    @Test
    @Transactional
    void putNonExistingProducto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        producto.setId(longCount.incrementAndGet());

        // Create the Producto
        ProductoDTO productoDTO = productoMapper.toDto(producto);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Producto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProducto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        producto.setId(longCount.incrementAndGet());

        // Create the Producto
        ProductoDTO productoDTO = productoMapper.toDto(producto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Producto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProducto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        producto.setId(longCount.incrementAndGet());

        // Create the Producto
        ProductoDTO productoDTO = productoMapper.toDto(producto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Producto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProductoWithPatch() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.saveAndFlush(producto);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the producto using partial update
        Producto partialUpdatedProducto = new Producto();
        partialUpdatedProducto.setId(producto.getId());

        partialUpdatedProducto
            .nombre(UPDATED_NOMBRE)
            .descripcion(UPDATED_DESCRIPCION)
            .precioBase(UPDATED_PRECIO_BASE)
            .personalizable(UPDATED_PERSONALIZABLE)
            .activo(UPDATED_ACTIVO);

        restProductoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProducto.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProducto))
            )
            .andExpect(status().isOk());

        // Validate the Producto in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductoUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedProducto, producto), getPersistedProducto(producto));
    }

    @Test
    @Transactional
    void fullUpdateProductoWithPatch() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.saveAndFlush(producto);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the producto using partial update
        Producto partialUpdatedProducto = new Producto();
        partialUpdatedProducto.setId(producto.getId());

        partialUpdatedProducto
            .nombre(UPDATED_NOMBRE)
            .descripcion(UPDATED_DESCRIPCION)
            .precioBase(UPDATED_PRECIO_BASE)
            .personalizable(UPDATED_PERSONALIZABLE)
            .plazoEstimadoDias(UPDATED_PLAZO_ESTIMADO_DIAS)
            .imagenUrl(UPDATED_IMAGEN_URL)
            .activo(UPDATED_ACTIVO);

        restProductoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProducto.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProducto))
            )
            .andExpect(status().isOk());

        // Validate the Producto in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductoUpdatableFieldsEquals(partialUpdatedProducto, getPersistedProducto(partialUpdatedProducto));
    }

    @Test
    @Transactional
    void patchNonExistingProducto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        producto.setId(longCount.incrementAndGet());

        // Create the Producto
        ProductoDTO productoDTO = productoMapper.toDto(producto);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Producto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProducto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        producto.setId(longCount.incrementAndGet());

        // Create the Producto
        ProductoDTO productoDTO = productoMapper.toDto(producto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Producto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProducto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        producto.setId(longCount.incrementAndGet());

        // Create the Producto
        ProductoDTO productoDTO = productoMapper.toDto(producto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(productoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Producto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProducto() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.saveAndFlush(producto);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the producto
        restProductoMockMvc
            .perform(delete(ENTITY_API_URL_ID, producto.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return productoRepository.count();
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

    protected Producto getPersistedProducto(Producto producto) {
        return productoRepository.findById(producto.getId()).orElseThrow();
    }

    protected void assertPersistedProductoToMatchAllProperties(Producto expectedProducto) {
        assertProductoAllPropertiesEquals(expectedProducto, getPersistedProducto(expectedProducto));
    }

    protected void assertPersistedProductoToMatchUpdatableProperties(Producto expectedProducto) {
        assertProductoAllUpdatablePropertiesEquals(expectedProducto, getPersistedProducto(expectedProducto));
    }
}
