package com.detallsublim.app.service;

import com.detallsublim.app.domain.SolicitudPresupuesto;
import com.detallsublim.app.domain.enumeration.EstadoSolicitud;
import com.detallsublim.app.repository.SolicitudPresupuestoRepository;
import com.detallsublim.app.service.dto.SolicitudPresupuestoDTO;
import com.detallsublim.app.service.mapper.SolicitudPresupuestoMapper;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
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
        LOG.debug("Request to save SolicitudPresupuesto");

        SolicitudPresupuesto solicitudPresupuesto = solicitudPresupuestoMapper.toEntity(solicitudPresupuestoDTO);

        solicitudPresupuesto = solicitudPresupuestoRepository.save(solicitudPresupuesto);

        String telefono = solicitudPresupuesto.getTelefono() != null && !solicitudPresupuesto.getTelefono().isBlank()
            ? solicitudPresupuesto.getTelefono()
            : "No indicado";

        String empresa = solicitudPresupuesto.getNombreEmpresa() != null && !solicitudPresupuesto.getNombreEmpresa().isBlank()
            ? solicitudPresupuesto.getNombreEmpresa()
            : "No indicada";

        String descripcion = solicitudPresupuesto.getDescripcion() != null && !solicitudPresupuesto.getDescripcion().isBlank()
            ? solicitudPresupuesto.getDescripcion()
            : "Sin descripción adicional";

        String producto = solicitudPresupuesto.getProducto() != null
            ? solicitudPresupuesto.getProducto().getNombre()
            : "Producto no especificado";

        String contenido =
            "Se ha recibido una nueva solicitud de presupuesto desde la web de Detall Sublim.\n\n" +
            "Cliente: " +
            solicitudPresupuesto.getNombreCliente() +
            "\n" +
            "Email: " +
            solicitudPresupuesto.getEmail() +
            "\n" +
            "Teléfono: " +
            telefono +
            "\n" +
            "Empresa: " +
            empresa +
            "\n\n" +
            "Producto: " +
            producto +
            "\n" +
            "Cantidad: " +
            solicitudPresupuesto.getCantidad() +
            "\n\n" +
            "Descripción:\n" +
            descripcion +
            "\n\n" +
            "ID de solicitud: #" +
            solicitudPresupuesto.getId();

        mailService.sendCompanyNotification("Nueva solicitud de presupuesto - Detall Sublim", contenido);

        return solicitudPresupuestoMapper.toDto(solicitudPresupuesto);
    }

    /**
     * Update a solicitudPresupuesto.
     *
     * @param solicitudPresupuestoDTO the entity to save.
     * @return the persisted entity.
     */
    public SolicitudPresupuestoDTO update(SolicitudPresupuestoDTO dto) {
        LOG.debug("Request to update SolicitudPresupuesto with id {}", dto.getId());

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

                String tiempo = solicitud.getTiempoEstimado() != null && !solicitud.getTiempoEstimado().isBlank()
                    ? solicitud.getTiempoEstimado()
                    : "Por definir";

                String observaciones = solicitud.getObservacionesPresupuesto() != null && !solicitud.getObservacionesPresupuesto().isBlank()
                    ? solicitud.getObservacionesPresupuesto()
                    : "Sin observaciones adicionales.";

                String producto = solicitud.getProducto() != null ? solicitud.getProducto().getNombre() : "Producto personalizado";

                Map<String, String> detalles = new LinkedHashMap<>();

                detalles.put("Producto", producto);
                detalles.put("Cantidad", solicitud.getCantidad() + " unidades");
                detalles.put("Precio estimado", precio);
                detalles.put("Tiempo estimado", tiempo);
                detalles.put("Referencia", "#" + solicitud.getId());

                mailService.sendBrandedDetailsEmail(
                    email,
                    "Presupuesto disponible - Detall Sublim",
                    "PRESUPUESTO",
                    "Tu presupuesto está listo",
                    "Hola " + nombre + ", hemos preparado el presupuesto correspondiente a tu solicitud.",
                    detalles,
                    observaciones
                );
            }
            case ACEPTADO -> {
                mailService.sendBrandedTextEmail(
                    email,
                    "Presupuesto aceptado - Detall Sublim",
                    "SOLICITUD ACEPTADA",
                    "Tu solicitud ha sido aceptada",
                    "Hola " +
                    nombre +
                    ",\n\n" +
                    "Tu solicitud ha sido aceptada correctamente.\n\n" +
                    "Nos pondremos en contacto contigo para continuar con el proceso.\n\n" +
                    "Gracias por confiar en Detall Sublim."
                );
            }
            case FINALIZADO -> {
                mailService.sendBrandedTextEmail(
                    email,
                    "Pedido finalizado - Detall Sublim",
                    "PEDIDO FINALIZADO",
                    "¡Tu pedido está terminado!",
                    "Hola " +
                    nombre +
                    ",\n\n" +
                    "Nos alegra informarte de que tu pedido ha sido finalizado.\n\n" +
                    "Gracias por elegir Detall Sublim para dar forma a tu idea. " +
                    "Esperamos que disfrutes mucho del resultado."
                );
            }
            case RECHAZADO -> {
                String motivo = solicitud.getObservacionesInternas() != null && !solicitud.getObservacionesInternas().isBlank()
                    ? solicitud.getObservacionesInternas()
                    : "No se ha especificado un motivo.";

                mailService.sendBrandedTextEmail(
                    email,
                    "Actualización de tu solicitud - Detall Sublim",
                    "SOLICITUD ACTUALIZADA",
                    "Información sobre tu solicitud",
                    "Hola " +
                    nombre +
                    ",\n\n" +
                    "En esta ocasión no podremos continuar con tu solicitud.\n\n" +
                    "Motivo:\n" +
                    motivo +
                    "\n\n" +
                    "Si necesitas alguna aclaración, puedes ponerte en contacto con nosotros."
                );
            }
            default -> {
                // PENDIENTE no envía correo al cliente.
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
        LOG.debug("Request to partially update SolicitudPresupuesto with id {}", solicitudPresupuestoDTO.getId());

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
