package com.detallsublim.app.service;

import com.detallsublim.app.domain.Historia;
import com.detallsublim.app.domain.HistoriaImagen;
import com.detallsublim.app.domain.enumeration.EstadoHistoria;
import com.detallsublim.app.repository.HistoriaImagenRepository;
import com.detallsublim.app.repository.HistoriaRepository;
import com.detallsublim.app.service.HistoriaServiceException;
import com.detallsublim.app.service.dto.HistoriaImagenDTO;
import com.detallsublim.app.service.mapper.HistoriaImagenMapper;
import com.detallsublim.app.service.storage.FileStorageService;
import com.detallsublim.app.service.validation.HistoriaImageValidationService;
import com.detallsublim.app.service.validation.HistoriaImageValidationService.ValidatedImage;
import java.io.IOException;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Lógica de negocio para gestionar las imágenes de las Historias.
 */
@Service
@Transactional
public class HistoriaImagenService {

    private static final Logger LOG = LoggerFactory.getLogger(HistoriaImagenService.class);

    private static final int MAX_IMAGES = 10;
    private static final int MAX_ALT_TEXT_LENGTH = 160;
    private static final int MAX_ORIGINAL_NAME_LENGTH = 255;

    private final HistoriaRepository historiaRepository;
    private final HistoriaImagenRepository historiaImagenRepository;
    private final HistoriaImagenMapper historiaImagenMapper;
    private final FileStorageService fileStorageService;
    private final HistoriaImageValidationService imageValidationService;

    public HistoriaImagenService(
        HistoriaRepository historiaRepository,
        HistoriaImagenRepository historiaImagenRepository,
        HistoriaImagenMapper historiaImagenMapper,
        FileStorageService fileStorageService,
        HistoriaImageValidationService imageValidationService
    ) {
        this.historiaRepository = historiaRepository;
        this.historiaImagenRepository = historiaImagenRepository;
        this.historiaImagenMapper = historiaImagenMapper;
        this.fileStorageService = fileStorageService;
        this.imageValidationService = imageValidationService;
    }

    /**
     * Añade una imagen a una Historia.
     */
    public HistoriaImagenDTO addImage(Long historiaId, MultipartFile file, String textoAlternativo, boolean portada) {
        LOG.debug("Request to add image to Historia : {}", historiaId);

        Historia historia = getHistoriaForUpdateOrThrow(historiaId);

        if (historia.getImagenes().size() >= MAX_IMAGES) {
            throw HistoriaServiceException.conflict("Una historia no puede contener más de 10 imágenes.");
        }

        validateAlternativeText(textoAlternativo);

        if (file == null || file.isEmpty()) {
            throw HistoriaServiceException.badRequest("La imagen es obligatoria.");
        }

        if (file.getSize() > HistoriaImageValidationService.MAX_FILE_BYTES) {
            throw HistoriaServiceException.badRequest("La imagen no puede superar los 5 MB.");
        }

        byte[] content = readContent(file);

        ValidatedImage validatedImage;

        try {
            validatedImage = imageValidationService.validate(content, file.getContentType());
        } catch (IllegalArgumentException e) {
            throw HistoriaServiceException.badRequest(e.getMessage());
        }

        String originalFilename = sanitizeOriginalFilename(file.getOriginalFilename(), validatedImage.extension());

        String storageKey = fileStorageService.store(content, validatedImage.extension());

        registerRollbackCleanup(storageKey);

        if (portada) {
            historia.getImagenes().forEach(imagen -> imagen.setPortada(false));
        }

        int nextOrder =
            historia
                .getImagenes()
                .stream()
                .map(HistoriaImagen::getOrden)
                .filter(orden -> orden != null)
                .max(Comparator.naturalOrder())
                .orElse(-1) +
            1;

        HistoriaImagen imagen = new HistoriaImagen();
        imagen.setStorageKey(storageKey);
        imagen.setNombreOriginal(originalFilename);
        imagen.setContentType(validatedImage.contentType());
        imagen.setTamanoBytes((long) content.length);
        imagen.setTextoAlternativo(StringUtils.hasText(textoAlternativo) ? textoAlternativo.strip() : null);
        imagen.setOrden(nextOrder);
        imagen.setPortada(portada);
        imagen.setHistoria(historia);

        imagen = historiaImagenRepository.save(imagen);

        historia.setFechaActualizacion(Instant.now());
        historiaRepository.save(historia);

        return toDto(imagen);
    }

    /**
     * Elimina una imagen de una Historia.
     *
     * El archivo físico se elimina únicamente después de que la
     * transacción de base de datos se haya confirmado.
     */
    public void deleteImage(Long historiaId, Long imagenId) {
        LOG.debug("Request to delete image {} from Historia {}", imagenId, historiaId);

        Historia historia = getHistoriaForUpdateOrThrow(historiaId);

        HistoriaImagen imagen = historia
            .getImagenes()
            .stream()
            .filter(item -> imagenId.equals(item.getId()))
            .findFirst()
            .orElseThrow(() -> HistoriaServiceException.notFound("La imagen indicada no pertenece a esta historia."));

        if (historia.getEstado() == EstadoHistoria.PUBLICADA && Boolean.TRUE.equals(imagen.getPortada())) {
            throw HistoriaServiceException.conflict(
                "No se puede eliminar la portada de una historia publicada. Selecciona primero otra portada."
            );
        }

        String storageKey = imagen.getStorageKey();

        historia.removeImagen(imagen);

        List<HistoriaImagen> remainingImages = historia
            .getImagenes()
            .stream()
            .sorted(Comparator.comparing(HistoriaImagen::getOrden))
            .toList();

        for (int index = 0; index < remainingImages.size(); index++) {
            remainingImages.get(index).setOrden(index);
        }

        historia.setFechaActualizacion(Instant.now());
        historiaRepository.save(historia);

        registerAfterCommitDeletion(storageKey);
    }

    /**
     * Establece una imagen como portada de la Historia.
     *
     * Cualquier portada anterior queda desmarcada dentro
     * de la misma transacción.
     */
    public HistoriaImagenDTO setCover(Long historiaId, Long imagenId) {
        LOG.debug("Request to set image {} as cover for Historia {}", imagenId, historiaId);

        if (imagenId == null) {
            throw HistoriaServiceException.badRequest("La imagen de portada es obligatoria.");
        }

        Historia historia = getHistoriaForUpdateOrThrow(historiaId);

        HistoriaImagen selectedImage = historia
            .getImagenes()
            .stream()
            .filter(imagen -> imagenId.equals(imagen.getId()))
            .findFirst()
            .orElseThrow(() -> HistoriaServiceException.notFound("La imagen indicada no pertenece a esta historia."));

        historia.getImagenes().forEach(imagen -> imagen.setPortada(imagenId.equals(imagen.getId())));

        historiaImagenRepository.saveAll(historia.getImagenes());

        historia.setFechaActualizacion(Instant.now());
        historiaRepository.save(historia);

        return toDto(selectedImage);
    }

    private Historia getHistoriaForUpdateOrThrow(Long historiaId) {
        Historia historia = historiaRepository
            .findByIdForUpdate(historiaId)
            .orElseThrow(() -> HistoriaServiceException.notFound("La historia indicada no existe."));

        historia.getImagenes().size();

        return historia;
    }

    private byte[] readContent(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer la imagen recibida.", e);
        }
    }

    private void validateAlternativeText(String textoAlternativo) {
        if (textoAlternativo != null && textoAlternativo.strip().length() > MAX_ALT_TEXT_LENGTH) {
            throw HistoriaServiceException.badRequest("El texto alternativo no puede superar los 160 caracteres.");
        }
    }

    private String sanitizeOriginalFilename(String originalFilename, String extension) {
        if (!StringUtils.hasText(originalFilename)) {
            return "imagen." + extension;
        }

        String cleaned = StringUtils.cleanPath(originalFilename.strip());

        int slashIndex = cleaned.lastIndexOf('/');

        if (slashIndex >= 0) {
            cleaned = cleaned.substring(slashIndex + 1);
        }

        if (cleaned.isBlank()) {
            cleaned = "imagen." + extension;
        }

        if (cleaned.length() > MAX_ORIGINAL_NAME_LENGTH) {
            cleaned = cleaned.substring(0, MAX_ORIGINAL_NAME_LENGTH);
        }

        return cleaned;
    }

    private HistoriaImagenDTO toDto(HistoriaImagen imagen) {
        HistoriaImagenDTO dto = historiaImagenMapper.toDto(imagen);
        dto.setUrl("/api/public/historias/imagenes/" + imagen.getId());
        return dto;
    }

    private void registerRollbackCleanup(String storageKey) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if (status != TransactionSynchronization.STATUS_COMMITTED) {
                        try {
                            fileStorageService.delete(storageKey);
                        } catch (RuntimeException e) {
                            LOG.warn("Could not remove Historia image after transaction rollback");
                        }
                    }
                }
            }
        );
    }

    private void registerAfterCommitDeletion(String storageKey) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            fileStorageService.delete(storageKey);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    try {
                        fileStorageService.delete(storageKey);
                    } catch (RuntimeException e) {
                        LOG.warn("Could not remove Historia image file after database commit");
                    }
                }
            }
        );
    }

    /**
     * Reordena las imágenes de una Historia.
     *
     * La lista debe contener exactamente todos los ids actuales
     * de las imágenes pertenecientes a la Historia, sin duplicados.
     */
    public void reorderImages(Long historiaId, List<Long> imageIds) {
        LOG.debug("Request to reorder images for Historia {}", historiaId);

        Historia historia = getHistoriaForUpdateOrThrow(historiaId);

        if (imageIds == null) {
            throw HistoriaServiceException.badRequest("El orden de imágenes es obligatorio.");
        }

        if (imageIds.size() != historia.getImagenes().size()) {
            throw HistoriaServiceException.badRequest("El orden debe incluir exactamente todas las imágenes de la historia.");
        }

        long distinctIds = imageIds.stream().distinct().count();

        if (distinctIds != imageIds.size()) {
            throw HistoriaServiceException.badRequest("El orden de imágenes contiene ids duplicados.");
        }

        for (Long imageId : imageIds) {
            boolean belongsToHistoria = historia
                .getImagenes()
                .stream()
                .anyMatch(imagen -> imageId != null && imageId.equals(imagen.getId()));

            if (!belongsToHistoria) {
                throw HistoriaServiceException.badRequest("Una de las imágenes indicadas no pertenece a esta historia.");
            }
        }

        for (int index = 0; index < imageIds.size(); index++) {
            Long imageId = imageIds.get(index);

            HistoriaImagen imagen = historia.getImagenes().stream().filter(item -> imageId.equals(item.getId())).findFirst().orElseThrow();

            imagen.setOrden(index);
        }

        historiaImagenRepository.saveAll(historia.getImagenes());

        historia.setFechaActualizacion(Instant.now());
        historiaRepository.save(historia);
    }

    /**
     * Recupera una imagen perteneciente a una Historia para administración.
     *
     * Permite consultar imágenes de Historias en BORRADOR o PUBLICADAS,
     * siempre que la imagen pertenezca a la Historia indicada.
     */
    @Transactional(readOnly = true)
    public PublicImage loadAdminImage(Long historiaId, Long imagenId) {
        LOG.debug("Request to load admin Historia image {} from Historia {}", imagenId, historiaId);

        if (historiaId == null || imagenId == null) {
            throw HistoriaServiceException.badRequest("La historia y la imagen son obligatorias.");
        }

        HistoriaImagen imagen = historiaImagenRepository
            .findOneByIdAndHistoriaId(imagenId, historiaId)
            .orElseThrow(() -> HistoriaServiceException.notFound("La imagen indicada no pertenece a esta historia."));

        String storageKey = imagen.getStorageKey();

        if (!fileStorageService.exists(storageKey)) {
            LOG.warn("Historia image file is missing for image id {}", imagenId);

            throw HistoriaServiceException.notFound("La imagen solicitada no existe.");
        }

        byte[] content = fileStorageService.load(storageKey);

        return new PublicImage(content, imagen.getContentType());
    }

    /**
     * Recupera una imagen perteneciente exclusivamente a una Historia publicada.
     */
    @Transactional(readOnly = true)
    public PublicImage loadPublishedImage(Long imagenId) {
        LOG.debug("Request to load published Historia image : {}", imagenId);

        if (imagenId == null) {
            throw HistoriaServiceException.badRequest("La imagen es obligatoria.");
        }

        HistoriaImagen imagen = historiaImagenRepository
            .findOneByIdAndHistoriaEstado(imagenId, EstadoHistoria.PUBLICADA)
            .orElseThrow(() -> HistoriaServiceException.notFound("La imagen solicitada no existe."));

        String storageKey = imagen.getStorageKey();

        if (!fileStorageService.exists(storageKey)) {
            LOG.warn("Published Historia image file is missing for image id {}", imagenId);

            throw HistoriaServiceException.notFound("La imagen solicitada no existe.");
        }

        byte[] content = fileStorageService.load(storageKey);

        return new PublicImage(content, imagen.getContentType());
    }

    public record PublicImage(byte[] content, String contentType) {}
}
