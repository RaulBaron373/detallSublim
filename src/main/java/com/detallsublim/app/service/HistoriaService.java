package com.detallsublim.app.service;

import com.detallsublim.app.domain.Historia;
import com.detallsublim.app.domain.HistoriaImagen;
import com.detallsublim.app.domain.enumeration.EstadoHistoria;
import com.detallsublim.app.repository.HistoriaRepository;
import com.detallsublim.app.service.dto.HistoriaDTO;
import com.detallsublim.app.service.dto.HistoriaResumenDTO;
import com.detallsublim.app.service.mapper.HistoriaMapper;
import com.detallsublim.app.service.storage.FileStorageService;
import java.text.Normalizer;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

/**
 * Lógica de negocio para gestionar Historias.
 */
@Service
@Transactional
public class HistoriaService {

    private static final Logger LOG = LoggerFactory.getLogger(HistoriaService.class);

    private static final int MAX_IMAGES = 10;
    private static final int MAX_SLUG_BASE_LENGTH = 160;

    private final HistoriaRepository historiaRepository;
    private final HistoriaMapper historiaMapper;
    private final FileStorageService fileStorageService;

    public HistoriaService(HistoriaRepository historiaRepository, HistoriaMapper historiaMapper, FileStorageService fileStorageService) {
        this.historiaRepository = historiaRepository;
        this.historiaMapper = historiaMapper;
        this.fileStorageService = fileStorageService;
    }

    /**
     * Crea una nueva historia.
     *
     * Toda historia nueva comienza como BORRADOR.
     * El slug y las fechas son generados por el servidor.
     */
    public HistoriaDTO create(HistoriaDTO historiaDTO) {
        LOG.debug("Request to create Historia");

        validateEditableFields(historiaDTO);

        Instant now = Instant.now();

        Historia historia = new Historia();
        historia.setTitulo(historiaDTO.getTitulo().strip());
        historia.setSlug(generateUniqueSlug(historiaDTO.getTitulo()));
        historia.setResumen(historiaDTO.getResumen().strip());
        historia.setContenido(historiaDTO.getContenido().strip());
        historia.setEstado(EstadoHistoria.BORRADOR);
        historia.setFechaCreacion(now);
        historia.setFechaActualizacion(now);
        historia.setFechaPublicacion(null);

        try {
            historia = historiaRepository.saveAndFlush(historia);
        } catch (DataIntegrityViolationException e) {
            throw HistoriaServiceException.conflict("No se pudo generar un slug único para la historia. Inténtalo de nuevo.");
        }

        return historiaMapper.toDto(historia);
    }

    /**
     * Actualiza los campos editables de una historia.
     *
     * No permite modificar slug, estado ni fechas desde el cliente.
     */
    public HistoriaDTO update(Long id, HistoriaDTO historiaDTO) {
        LOG.debug("Request to update Historia : {}", id);

        validateEditableFields(historiaDTO);

        Historia historia = getHistoriaWithImagesOrThrow(id);

        historia.setTitulo(historiaDTO.getTitulo().strip());
        historia.setResumen(historiaDTO.getResumen().strip());
        historia.setContenido(historiaDTO.getContenido().strip());
        historia.setFechaActualizacion(Instant.now());

        historia = historiaRepository.save(historia);

        return historiaMapper.toDto(historia);
    }

    /**
     * Publica una historia.
     *
     * Requiere contenido válido y exactamente una imagen de portada.
     * La fecha de publicación se establece únicamente la primera vez.
     */
    public HistoriaDTO publish(Long id) {
        LOG.debug("Request to publish Historia : {}", id);

        Historia historia = getHistoriaWithImagesOrThrow(id);

        validateHistoriaForPublication(historia);

        Instant now = Instant.now();

        if (historia.getFechaPublicacion() == null) {
            historia.setFechaPublicacion(now);
        }

        historia.setEstado(EstadoHistoria.PUBLICADA);
        historia.setFechaActualizacion(now);

        historia = historiaRepository.save(historia);

        return historiaMapper.toDto(historia);
    }

    /**
     * Retira una historia de la zona pública.
     *
     * Se conserva la fecha de su primera publicación.
     */
    public HistoriaDTO unpublish(Long id) {
        LOG.debug("Request to unpublish Historia : {}", id);

        Historia historia = getHistoriaWithImagesOrThrow(id);

        historia.setEstado(EstadoHistoria.BORRADOR);
        historia.setFechaActualizacion(Instant.now());

        historia = historiaRepository.save(historia);

        return historiaMapper.toDto(historia);
    }

    /**
     * Elimina una Historia y todas sus imágenes asociadas.
     *
     * Los archivos físicos se eliminan únicamente después
     * de confirmar la transacción de base de datos.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Historia : {}", id);

        Historia historia = getHistoriaWithImagesOrThrow(id);

        List<String> storageKeys = historia.getImagenes().stream().map(HistoriaImagen::getStorageKey).filter(StringUtils::hasText).toList();

        historiaRepository.delete(historia);

        registerAfterCommitFileDeletion(storageKeys);
    }

    /**
     * Obtiene todas las historias para administración.
     */
    @Transactional(readOnly = true)
    public Page<HistoriaDTO> findAllForAdmin(Pageable pageable) {
        return historiaRepository.findAll(pageable).map(historiaMapper::toDto);
    }

    /**
     * Obtiene una historia por id para administración.
     */
    @Transactional(readOnly = true)
    public Optional<HistoriaDTO> findOneForAdmin(Long id) {
        return historiaRepository.findOneWithImagenesById(id).map(historiaMapper::toDto);
    }

    /**
     * Obtiene únicamente historias publicadas para la web pública.
     */
    @Transactional(readOnly = true)
    public Page<HistoriaResumenDTO> findPublished(Pageable pageable) {
        return historiaRepository.findPublishedSummaries(EstadoHistoria.PUBLICADA, pageable);
    }

    /**
     * Obtiene una historia publicada por slug.
     */
    @Transactional(readOnly = true)
    public Optional<HistoriaDTO> findPublishedBySlug(String slug) {
        return historiaRepository.findOneWithImagenesBySlugAndEstado(slug, EstadoHistoria.PUBLICADA).map(historiaMapper::toDto);
    }

    private Historia getHistoriaWithImagesOrThrow(Long id) {
        Historia historia = historiaRepository
            .findByIdForUpdate(id)
            .orElseThrow(() -> HistoriaServiceException.notFound("La historia indicada no existe."));

        historia.getImagenes().size();

        return historia;
    }

    private void validateEditableFields(HistoriaDTO historiaDTO) {
        if (historiaDTO == null) {
            throw HistoriaServiceException.badRequest("La historia es obligatoria.");
        }

        if (!StringUtils.hasText(historiaDTO.getTitulo())) {
            throw HistoriaServiceException.badRequest("El título de la historia es obligatorio.");
        }

        if (!StringUtils.hasText(historiaDTO.getResumen())) {
            throw HistoriaServiceException.badRequest("El resumen de la historia es obligatorio.");
        }

        if (!StringUtils.hasText(historiaDTO.getContenido())) {
            throw HistoriaServiceException.badRequest("El contenido de la historia es obligatorio.");
        }
    }

    private void validateHistoriaForPublication(Historia historia) {
        if (!StringUtils.hasText(historia.getTitulo())) {
            throw HistoriaServiceException.conflict("No se puede publicar una historia sin título.");
        }

        if (!StringUtils.hasText(historia.getResumen())) {
            throw HistoriaServiceException.conflict("No se puede publicar una historia sin resumen.");
        }

        if (!StringUtils.hasText(historia.getContenido())) {
            throw HistoriaServiceException.conflict("No se puede publicar una historia sin contenido.");
        }

        if (historia.getImagenes().size() > MAX_IMAGES) {
            throw HistoriaServiceException.conflict("Una historia no puede contener más de 10 imágenes.");
        }

        long coverCount = historia.getImagenes().stream().filter(imagen -> Boolean.TRUE.equals(imagen.getPortada())).count();

        if (coverCount == 0) {
            throw HistoriaServiceException.conflict("La historia necesita una imagen de portada antes de publicarse.");
        }

        if (coverCount > 1) {
            throw HistoriaServiceException.conflict("La historia no puede tener más de una imagen de portada.");
        }
    }

    private String generateUniqueSlug(String titulo) {
        String baseSlug = Normalizer.normalize(titulo, Normalizer.Form.NFD)
            .replaceAll("\\p{M}+", "")
            .toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("^-+|-+$", "");

        if (baseSlug.isBlank()) {
            baseSlug = "historia";
        }

        if (baseSlug.length() > MAX_SLUG_BASE_LENGTH) {
            baseSlug = baseSlug.substring(0, MAX_SLUG_BASE_LENGTH).replaceAll("-+$", "");
        }

        String candidate = baseSlug;
        int suffix = 2;

        while (historiaRepository.existsBySlug(candidate)) {
            candidate = baseSlug + "-" + suffix;
            suffix++;
        }

        return candidate;
    }

    private void registerAfterCommitFileDeletion(List<String> storageKeys) {
        if (storageKeys == null || storageKeys.isEmpty()) {
            return;
        }

        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            storageKeys.forEach(this::deleteFileSafely);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    storageKeys.forEach(HistoriaService.this::deleteFileSafely);
                }
            }
        );
    }

    private void deleteFileSafely(String storageKey) {
        try {
            fileStorageService.delete(storageKey);
        } catch (RuntimeException e) {
            LOG.warn("Could not remove Historia file after database commit");
        }
    }
}
