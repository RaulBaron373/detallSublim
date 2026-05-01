package com.detallsublim.app.service;

import com.detallsublim.app.domain.SolicitudPresupuesto;
import com.detallsublim.app.repository.SolicitudPresupuestoRepository;
import com.detallsublim.app.service.dto.SolicitudPresupuestoDTO;
import com.detallsublim.app.service.mapper.SolicitudPresupuestoMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.detallsublim.app.domain.SolicitudPresupuesto}.
 */
@Service
@Transactional
public class SolicitudPresupuestoService {

    private static final Logger LOG = LoggerFactory.getLogger(SolicitudPresupuestoService.class);

    private final SolicitudPresupuestoRepository solicitudPresupuestoRepository;

    private final SolicitudPresupuestoMapper solicitudPresupuestoMapper;

    public SolicitudPresupuestoService(
        SolicitudPresupuestoRepository solicitudPresupuestoRepository,
        SolicitudPresupuestoMapper solicitudPresupuestoMapper
    ) {
        this.solicitudPresupuestoRepository = solicitudPresupuestoRepository;
        this.solicitudPresupuestoMapper = solicitudPresupuestoMapper;
    }

    /**
     * Save a solicitudPresupuesto.
     *
     * @param solicitudPresupuestoDTO the entity to save.
     * @return the persisted entity.
     */
    public SolicitudPresupuestoDTO save(SolicitudPresupuestoDTO solicitudPresupuestoDTO) {
        LOG.debug("Request to save SolicitudPresupuesto : {}", solicitudPresupuestoDTO);
        SolicitudPresupuesto solicitudPresupuesto = solicitudPresupuestoMapper.toEntity(solicitudPresupuestoDTO);
        solicitudPresupuesto = solicitudPresupuestoRepository.save(solicitudPresupuesto);
        return solicitudPresupuestoMapper.toDto(solicitudPresupuesto);
    }

    /**
     * Update a solicitudPresupuesto.
     *
     * @param solicitudPresupuestoDTO the entity to save.
     * @return the persisted entity.
     */
    public SolicitudPresupuestoDTO update(SolicitudPresupuestoDTO solicitudPresupuestoDTO) {
        LOG.debug("Request to update SolicitudPresupuesto : {}", solicitudPresupuestoDTO);
        SolicitudPresupuesto solicitudPresupuesto = solicitudPresupuestoMapper.toEntity(solicitudPresupuestoDTO);
        solicitudPresupuesto = solicitudPresupuestoRepository.save(solicitudPresupuesto);
        return solicitudPresupuestoMapper.toDto(solicitudPresupuesto);
    }

    /**
     * Partially update a solicitudPresupuesto.
     *
     * @param solicitudPresupuestoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SolicitudPresupuestoDTO> partialUpdate(SolicitudPresupuestoDTO solicitudPresupuestoDTO) {
        LOG.debug("Request to partially update SolicitudPresupuesto : {}", solicitudPresupuestoDTO);

        return solicitudPresupuestoRepository
            .findById(solicitudPresupuestoDTO.getId())
            .map(existingSolicitudPresupuesto -> {
                solicitudPresupuestoMapper.partialUpdate(existingSolicitudPresupuesto, solicitudPresupuestoDTO);

                return existingSolicitudPresupuesto;
            })
            .map(solicitudPresupuestoRepository::save)
            .map(solicitudPresupuestoMapper::toDto);
    }

    /**
     * Get all the solicitudPresupuestos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SolicitudPresupuestoDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all SolicitudPresupuestos");
        return solicitudPresupuestoRepository.findAll(pageable).map(solicitudPresupuestoMapper::toDto);
    }

    /**
     * Get all the solicitudPresupuestos with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<SolicitudPresupuestoDTO> findAllWithEagerRelationships(Pageable pageable) {
        return solicitudPresupuestoRepository.findAllWithEagerRelationships(pageable).map(solicitudPresupuestoMapper::toDto);
    }

    /**
     * Get one solicitudPresupuesto by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SolicitudPresupuestoDTO> findOne(Long id) {
        LOG.debug("Request to get SolicitudPresupuesto : {}", id);
        return solicitudPresupuestoRepository.findOneWithEagerRelationships(id).map(solicitudPresupuestoMapper::toDto);
    }

    /**
     * Delete the solicitudPresupuesto by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete SolicitudPresupuesto : {}", id);
        solicitudPresupuestoRepository.deleteById(id);
    }
}
