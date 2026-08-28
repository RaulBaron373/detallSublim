package com.detallsublim.app.service;

import com.detallsublim.app.domain.Producto;
import com.detallsublim.app.repository.ProductoRepository;
import com.detallsublim.app.service.dto.ProductoDTO;
import com.detallsublim.app.service.mapper.ProductoMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.detallsublim.app.domain.Producto}.
 */
@Service
@Transactional
public class ProductoService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductoService.class);

    private final ProductoRepository productoRepository;

    private final ProductoMapper productoMapper;

    public ProductoService(ProductoRepository productoRepository, ProductoMapper productoMapper) {
        this.productoRepository = productoRepository;
        this.productoMapper = productoMapper;
    }

    /**
     * Save a producto.
     *
     * @param productoDTO the entity to save.
     * @return the persisted entity.
     */
    public ProductoDTO save(ProductoDTO productoDTO) {
        LOG.debug("Request to save Producto");
        Producto producto = productoMapper.toEntity(productoDTO);
        producto = productoRepository.save(producto);
        return productoMapper.toDto(producto);
    }

    /**
     * Update a producto.
     *
     * @param productoDTO the entity to save.
     * @return the persisted entity.
     */
    public ProductoDTO update(ProductoDTO productoDTO) {
        LOG.debug("Request to update Producto with id {}", productoDTO.getId());
        Producto producto = productoMapper.toEntity(productoDTO);
        producto = productoRepository.save(producto);
        return productoMapper.toDto(producto);
    }

    /**
     * Partially update a producto.
     *
     * @param productoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ProductoDTO> partialUpdate(ProductoDTO productoDTO) {
        LOG.debug("Request to partially update Producto with id {}", productoDTO.getId());

        return productoRepository
            .findById(productoDTO.getId())
            .map(existingProducto -> {
                productoMapper.partialUpdate(existingProducto, productoDTO);

                return existingProducto;
            })
            .map(productoRepository::save)
            .map(productoMapper::toDto);
    }

    /**
     * Get all the productos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ProductoDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Productos");
        return productoRepository.findAll(pageable).map(productoMapper::toDto);
    }

    /**
     * Get all the productos with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ProductoDTO> findAllWithEagerRelationships(Pageable pageable) {
        return productoRepository.findAllWithEagerRelationships(pageable).map(productoMapper::toDto);
    }

    /**
     * Get one producto by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ProductoDTO> findOne(Long id) {
        LOG.debug("Request to get Producto : {}", id);
        return productoRepository.findOneWithEagerRelationships(id).map(productoMapper::toDto);
    }

    /**
     * Delete the producto by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Producto : {}", id);
        productoRepository.deleteById(id);
    }
}
