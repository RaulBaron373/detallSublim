package com.detallsublim.app.service;

import com.detallsublim.app.domain.MensajeContacto;
import com.detallsublim.app.repository.MensajeContactoRepository;
import com.detallsublim.app.service.dto.MensajeContactoDTO;
import com.detallsublim.app.service.mapper.MensajeContactoMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.detallsublim.app.domain.MensajeContacto}.
 */
@Service
@Transactional
public class MensajeContactoService {

    private static final Logger LOG = LoggerFactory.getLogger(MensajeContactoService.class);

    private final MensajeContactoRepository mensajeContactoRepository;

    private final MensajeContactoMapper mensajeContactoMapper;
    private final MailService mailService;

    public MensajeContactoService(
        MensajeContactoRepository mensajeContactoRepository,
        MensajeContactoMapper mensajeContactoMapper,
        MailService mailService
    ) {
        this.mensajeContactoRepository = mensajeContactoRepository;
        this.mensajeContactoMapper = mensajeContactoMapper;
        this.mailService = mailService;
    }

    /**
     * Save a mensajeContacto.
     *
     * @param mensajeContactoDTO the entity to save.
     * @return the persisted entity.
     */
    public MensajeContactoDTO save(MensajeContactoDTO mensajeContactoDTO) {
        LOG.debug("Request to save MensajeContacto : {}", mensajeContactoDTO);

        MensajeContacto mensajeContacto = mensajeContactoMapper.toEntity(mensajeContactoDTO);
        mensajeContacto = mensajeContactoRepository.save(mensajeContacto);

        String telefono = mensajeContacto.getTelefono() != null && !mensajeContacto.getTelefono().isBlank()
            ? mensajeContacto.getTelefono()
            : "No indicado";

        String mensaje = mensajeContacto.getMensaje() != null && !mensajeContacto.getMensaje().isBlank()
            ? mensajeContacto.getMensaje()
            : "Sin mensaje";

        String contenido =
            "Se ha recibido un nuevo mensaje desde la web de Detall Sublim.\n\n" +
            "Nombre: " +
            mensajeContacto.getNombre() +
            "\n" +
            "Email: " +
            mensajeContacto.getEmail() +
            "\n" +
            "Teléfono: " +
            telefono +
            "\n" +
            "Asunto: " +
            mensajeContacto.getAsunto() +
            "\n\n" +
            "Mensaje:\n" +
            mensaje +
            "\n\n" +
            "ID del mensaje: #" +
            mensajeContacto.getId();

        mailService.sendCompanyNotification("Nuevo mensaje de contacto - Detall Sublim", contenido);

        return mensajeContactoMapper.toDto(mensajeContacto);
    }

    /**
     * Update a mensajeContacto.
     *
     * @param mensajeContactoDTO the entity to save.
     * @return the persisted entity.
     */
    public MensajeContactoDTO update(MensajeContactoDTO mensajeContactoDTO) {
        LOG.debug("Request to update MensajeContacto : {}", mensajeContactoDTO);
        MensajeContacto mensajeContacto = mensajeContactoMapper.toEntity(mensajeContactoDTO);
        mensajeContacto = mensajeContactoRepository.save(mensajeContacto);
        return mensajeContactoMapper.toDto(mensajeContacto);
    }

    /**
     * Partially update a mensajeContacto.
     *
     * @param mensajeContactoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MensajeContactoDTO> partialUpdate(MensajeContactoDTO mensajeContactoDTO) {
        LOG.debug("Request to partially update MensajeContacto : {}", mensajeContactoDTO);

        return mensajeContactoRepository
            .findById(mensajeContactoDTO.getId())
            .map(existingMensajeContacto -> {
                mensajeContactoMapper.partialUpdate(existingMensajeContacto, mensajeContactoDTO);

                return existingMensajeContacto;
            })
            .map(mensajeContactoRepository::save)
            .map(mensajeContactoMapper::toDto);
    }

    /**
     * Get all the mensajeContactos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<MensajeContactoDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all MensajeContactos");
        return mensajeContactoRepository.findAll(pageable).map(mensajeContactoMapper::toDto);
    }

    /**
     * Get one mensajeContacto by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MensajeContactoDTO> findOne(Long id) {
        LOG.debug("Request to get MensajeContacto : {}", id);
        return mensajeContactoRepository.findById(id).map(mensajeContactoMapper::toDto);
    }

    /**
     * Delete the mensajeContacto by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete MensajeContacto : {}", id);
        mensajeContactoRepository.deleteById(id);
    }

    public void responderMensaje(Long id, String respuesta) {
        MensajeContacto mensaje = mensajeContactoRepository.findById(id).orElseThrow(() -> new RuntimeException("Mensaje no encontrado"));

        // enviar email
        String contenido =
            "Hola " +
            mensaje.getNombre() +
            ",\n\n" +
            "Hemos revisado tu consulta y queremos darte la siguiente respuesta:\n\n" +
            respuesta +
            "\n\nSi necesitas cualquier otra aclaración, puedes volver a ponerte en contacto con nosotros.";

        mailService.sendBrandedTextEmail(
            mensaje.getEmail(),
            "Respuesta a tu consulta - Detall Sublim",
            "RESPUESTA",
            "Tenemos una respuesta para ti",
            contenido
        );

        // marcar como atendido
        mensaje.setAtendido(true);

        mensajeContactoRepository.save(mensaje);
    }
}
