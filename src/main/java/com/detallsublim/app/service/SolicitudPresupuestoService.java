package com.detallsublim.app.service;

import com.detallsublim.app.domain.SolicitudPresupuesto;
import com.detallsublim.app.domain.enumeration.EstadoSolicitud;
import com.detallsublim.app.repository.SolicitudPresupuestoRepository;
import com.detallsublim.app.service.dto.SolicitudPresupuestoDTO;
import com.detallsublim.app.service.mapper.SolicitudPresupuestoMapper;
import java.time.Instant;
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

    private final MailService mailService;

    public SolicitudPresupuestoService(
        SolicitudPresupuestoRepository solicitudPresupuestoRepository,
        SolicitudPresupuestoMapper solicitudPresupuestoMapper,
        MailService mailService
    ) {
        this.solicitudPresupuestoRepository = solicitudPresupuestoRepository;
        this.solicitudPresupuestoMapper = solicitudPresupuestoMapper;
        this.mailService = mailService;
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
    public SolicitudPresupuestoDTO update(SolicitudPresupuestoDTO dto) {
        LOG.debug("Request to update SolicitudPresupuesto : {}", dto);

        SolicitudPresupuesto existing = solicitudPresupuestoRepository
            .findById(dto.getId())
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        EstadoSolicitud estadoAnterior = existing.getEstado();

        boolean primerEnvioPresupuesto = dto.getEstado() == EstadoSolicitud.PRESUPUESTADO && existing.getFechaEnvioPresupuesto() == null;

        SolicitudPresupuesto updated = solicitudPresupuestoMapper.toEntity(dto);

        /*
         * Conservamos siempre la fecha original
         * del primer envío del presupuesto.
         */
        if (existing.getFechaEnvioPresupuesto() != null) {
            updated.setFechaEnvioPresupuesto(existing.getFechaEnvioPresupuesto());
        }

        /*
         * La primera vez que pasa a PRESUPUESTADO
         * registramos cuándo se envió.
         */
        if (primerEnvioPresupuesto) {
            updated.setFechaEnvioPresupuesto(Instant.now());
        }

        updated = solicitudPresupuestoRepository.save(updated);

        /*
         * Enviar email únicamente si cambia el estado.
         */
        if (estadoAnterior != updated.getEstado()) {
            /*
             * El correo completo del presupuesto
             * se envía solamente la primera vez.
             */
            if (updated.getEstado() == EstadoSolicitud.PRESUPUESTADO) {
                if (primerEnvioPresupuesto) {
                    enviarEmailEstado(updated);
                }
            } else {
                /*
                 * ACEPTADO, FINALIZADO y RECHAZADO
                 * conservan sus emails actuales.
                 */
                enviarEmailEstado(updated);
            }
        }

        return solicitudPresupuestoMapper.toDto(updated);
    }

    private void enviarEmailEstado(SolicitudPresupuesto solicitud) {
        String email = solicitud.getEmail();
        String nombre = solicitud.getNombreCliente();

        switch (solicitud.getEstado()) {
            case PRESUPUESTADO -> {
                String precio = solicitud.getPrecioPresupuesto() != null ? solicitud.getPrecioPresupuesto() + " €" : "Por definir";

                String tiempo = solicitud.getTiempoEstimado() != null ? solicitud.getTiempoEstimado() : "Por definir";

                String observaciones = solicitud.getObservacionesPresupuesto() != null
                    ? solicitud.getObservacionesPresupuesto()
                    : "Sin observaciones adicionales.";

                String producto = solicitud.getProducto() != null ? solicitud.getProducto().getNombre() : "Producto personalizado";

                String mensaje =
                    "Hola " +
                    nombre +
                    ",\n\n" +
                    "Hemos preparado un presupuesto para tu solicitud.\n\n" +
                    "Producto: " +
                    producto +
                    "\n" +
                    "Cantidad: " +
                    solicitud.getCantidad() +
                    "\n" +
                    "Precio estimado: " +
                    precio +
                    "\n" +
                    "Tiempo estimado: " +
                    tiempo +
                    "\n\n" +
                    "Observaciones:\n" +
                    observaciones +
                    "\n\n" +
                    "Gracias por confiar en Detall Sublim.";

                mailService.sendEmail(email, "Presupuesto disponible - Detall Sublim", mensaje, false, false);
            }
            case ACEPTADO -> {
                mailService.sendEmail(
                    email,
                    "Presupuesto aceptado - Detall Sublim",
                    "Hola " + nombre + ", tu solicitud ha sido aceptada. Nos pondremos en contacto contigo pronto.",
                    false,
                    false
                );
            }
            case FINALIZADO -> {
                mailService.sendEmail(
                    email,
                    "Pedido finalizado - Detall Sublim",
                    "Hola " + nombre + ", tu pedido ha sido finalizado. Gracias por confiar en nosotros.",
                    false,
                    false
                );
            }
            case RECHAZADO -> {
                String motivo = solicitud.getObservacionesInternas() != null
                    ? solicitud.getObservacionesInternas()
                    : "No se ha especificado motivo.";

                mailService.sendEmail(
                    email,
                    "Solicitud rechazada - Detall Sublim",
                    "Hola " + nombre + ", tu solicitud ha sido rechazada.\nMotivo: " + motivo,
                    false,
                    false
                );
            }
            default -> {
                // no hacer nada para PENDIENTE
            }
        }
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
