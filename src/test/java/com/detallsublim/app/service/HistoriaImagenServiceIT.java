package com.detallsublim.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.detallsublim.app.IntegrationTest;
import com.detallsublim.app.domain.Historia;
import com.detallsublim.app.domain.HistoriaImagen;
import com.detallsublim.app.domain.enumeration.EstadoHistoria;
import com.detallsublim.app.repository.HistoriaImagenRepository;
import com.detallsublim.app.repository.HistoriaRepository;
import com.detallsublim.app.service.storage.FileStorageService;
import com.detallsublim.app.service.validation.HistoriaImageValidationService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

@IntegrationTest
class HistoriaImagenServiceIT {

    @Autowired
    private HistoriaImagenService historiaImagenService;

    @Autowired
    private HistoriaRepository historiaRepository;

    @Autowired
    private HistoriaImagenRepository historiaImagenRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @AfterEach
    void cleanup() {
        historiaImagenRepository
            .findAll()
            .forEach(imagen -> {
                String storageKey = imagen.getStorageKey();

                if (storageKey != null && !storageKey.isBlank() && fileStorageService.exists(storageKey)) {
                    fileStorageService.delete(storageKey);
                }
            });

        historiaImagenRepository.deleteAll();
        historiaImagenRepository.flush();

        historiaRepository.deleteAll();
        historiaRepository.flush();
    }

    @Test
    void addImageShouldAcceptPngAndPersistMetadata() {
        Historia historia = persistHistoria(EstadoHistoria.BORRADOR);

        MockMultipartFile file = multipart("imagen.png", "image/png", validPng());

        historiaImagenService.addImage(historia.getId(), file, "  Texto alternativo  ", true);

        List<HistoriaImagen> imagenes = historiaImagenRepository.findAllByHistoriaIdOrderByOrdenAsc(historia.getId());

        assertEquals(1, imagenes.size());

        HistoriaImagen imagen = imagenes.get(0);

        assertEquals("imagen.png", imagen.getNombreOriginal());
        assertEquals("image/png", imagen.getContentType());
        assertEquals((long) validPng().length, imagen.getTamanoBytes());
        assertEquals("Texto alternativo", imagen.getTextoAlternativo());
        assertEquals(0, imagen.getOrden());
        assertTrue(imagen.getPortada());
        assertTrue(fileStorageService.exists(imagen.getStorageKey()));
    }

    @Test
    void addImageShouldAcceptJpeg() {
        Historia historia = persistHistoria(EstadoHistoria.BORRADOR);

        historiaImagenService.addImage(historia.getId(), multipart("imagen.jpg", "image/jpeg", validJpeg()), null, false);

        HistoriaImagen imagen = historiaImagenRepository.findAllByHistoriaIdOrderByOrdenAsc(historia.getId()).get(0);

        assertEquals("image/jpeg", imagen.getContentType());
        assertTrue(fileStorageService.exists(imagen.getStorageKey()));
    }

    @Test
    void addImageShouldAcceptWebp() {
        Historia historia = persistHistoria(EstadoHistoria.BORRADOR);

        historiaImagenService.addImage(historia.getId(), multipart("imagen.webp", "image/webp", validWebp()), null, false);

        HistoriaImagen imagen = historiaImagenRepository.findAllByHistoriaIdOrderByOrdenAsc(historia.getId()).get(0);

        assertEquals("image/webp", imagen.getContentType());
        assertTrue(fileStorageService.exists(imagen.getStorageKey()));
    }

    @Test
    void addImageShouldRejectFileLargerThanFiveMegabytes() {
        Historia historia = persistHistoria(EstadoHistoria.BORRADOR);

        byte[] content = new byte[(int) HistoriaImageValidationService.MAX_FILE_BYTES + 1];

        MockMultipartFile file = multipart("demasiado-grande.png", "image/png", content);

        assertThrows(HistoriaServiceException.class, () -> historiaImagenService.addImage(historia.getId(), file, null, false));

        assertEquals(0, historiaImagenRepository.countByHistoriaId(historia.getId()));
    }

    @Test
    void addImageShouldRejectInvalidMagicBytes() {
        Historia historia = persistHistoria(EstadoHistoria.BORRADOR);

        MockMultipartFile file = multipart("falso.png", "image/png", new byte[] { 1, 2, 3, 4, 5, 6 });

        assertThrows(HistoriaServiceException.class, () -> historiaImagenService.addImage(historia.getId(), file, null, false));

        assertEquals(0, historiaImagenRepository.countByHistoriaId(historia.getId()));
    }

    @Test
    void addImageShouldRejectMismatchedContentType() {
        Historia historia = persistHistoria(EstadoHistoria.BORRADOR);

        MockMultipartFile file = multipart("imagen.png", "image/jpeg", validPng());

        assertThrows(HistoriaServiceException.class, () -> historiaImagenService.addImage(historia.getId(), file, null, false));

        assertEquals(0, historiaImagenRepository.countByHistoriaId(historia.getId()));
    }

    @Test
    void addImageShouldRejectAlternativeTextLongerThan160Characters() {
        Historia historia = persistHistoria(EstadoHistoria.BORRADOR);

        MockMultipartFile file = multipart("imagen.png", "image/png", validPng());

        String alternativeText = "a".repeat(161);

        assertThrows(HistoriaServiceException.class, () -> historiaImagenService.addImage(historia.getId(), file, alternativeText, false));

        assertEquals(0, historiaImagenRepository.countByHistoriaId(historia.getId()));
    }

    @Test
    void addImageShouldRejectMoreThanTenImages() {
        Historia historia = persistHistoria(EstadoHistoria.BORRADOR);

        for (int index = 0; index < 10; index++) {
            historia.addImagen(metadataImage(UUID.randomUUID() + ".png", index, index == 0));
        }

        historiaRepository.saveAndFlush(historia);

        assertEquals(10, historiaImagenRepository.countByHistoriaId(historia.getId()));

        MockMultipartFile eleventhImage = multipart("imagen-11.png", "image/png", validPng());

        assertThrows(HistoriaServiceException.class, () -> historiaImagenService.addImage(historia.getId(), eleventhImage, null, false));

        assertEquals(10, historiaImagenRepository.countByHistoriaId(historia.getId()));
    }

    @Test
    void addImageShouldAssignSequentialOrder() {
        Historia historia = persistHistoria(EstadoHistoria.BORRADOR);

        historiaImagenService.addImage(historia.getId(), multipart("primera.png", "image/png", validPng()), null, false);

        historiaImagenService.addImage(historia.getId(), multipart("segunda.png", "image/png", validPng()), null, false);

        List<HistoriaImagen> imagenes = historiaImagenRepository.findAllByHistoriaIdOrderByOrdenAsc(historia.getId());

        assertEquals(2, imagenes.size());
        assertEquals(0, imagenes.get(0).getOrden());
        assertEquals(1, imagenes.get(1).getOrden());
    }

    @Test
    void addingNewCoverShouldUnsetPreviousCover() {
        Historia historia = persistHistoria(EstadoHistoria.BORRADOR);

        historiaImagenService.addImage(historia.getId(), multipart("primera.png", "image/png", validPng()), null, true);

        historiaImagenService.addImage(historia.getId(), multipart("segunda.png", "image/png", validPng()), null, true);

        List<HistoriaImagen> imagenes = historiaImagenRepository.findAllByHistoriaIdOrderByOrdenAsc(historia.getId());

        assertEquals(2, imagenes.size());
        assertFalse(imagenes.get(0).getPortada());
        assertTrue(imagenes.get(1).getPortada());

        long coverCount = imagenes.stream().filter(imagen -> Boolean.TRUE.equals(imagen.getPortada())).count();

        assertEquals(1, coverCount);
    }

    @Test
    void setCoverShouldLeaveExactlyOneCover() {
        Historia historia = persistHistoria(EstadoHistoria.BORRADOR);

        historiaImagenService.addImage(historia.getId(), multipart("primera.png", "image/png", validPng()), null, true);

        historiaImagenService.addImage(historia.getId(), multipart("segunda.png", "image/png", validPng()), null, false);

        List<HistoriaImagen> before = historiaImagenRepository.findAllByHistoriaIdOrderByOrdenAsc(historia.getId());

        Long secondImageId = before.get(1).getId();

        historiaImagenService.setCover(historia.getId(), secondImageId);

        List<HistoriaImagen> after = historiaImagenRepository.findAllByHistoriaIdOrderByOrdenAsc(historia.getId());

        assertFalse(after.get(0).getPortada());
        assertTrue(after.get(1).getPortada());

        long coverCount = after.stream().filter(imagen -> Boolean.TRUE.equals(imagen.getPortada())).count();

        assertEquals(1, coverCount);
    }

    @Test
    void setCoverShouldRejectImageFromAnotherHistoria() {
        Historia historia1 = persistHistoria(EstadoHistoria.BORRADOR);
        Historia historia2 = persistHistoria(EstadoHistoria.BORRADOR);

        historiaImagenService.addImage(historia2.getId(), multipart("otra.png", "image/png", validPng()), null, true);

        Long foreignImageId = historiaImagenRepository.findAllByHistoriaIdOrderByOrdenAsc(historia2.getId()).get(0).getId();

        assertThrows(HistoriaServiceException.class, () -> historiaImagenService.setCover(historia1.getId(), foreignImageId));
    }

    @Test
    void deleteImageShouldDeleteFileAndReorderRemainingImages() {
        Historia historia = persistHistoria(EstadoHistoria.BORRADOR);

        historiaImagenService.addImage(historia.getId(), multipart("primera.png", "image/png", validPng()), null, true);

        historiaImagenService.addImage(historia.getId(), multipart("segunda.png", "image/png", validPng()), null, false);

        historiaImagenService.addImage(historia.getId(), multipart("tercera.png", "image/png", validPng()), null, false);

        List<HistoriaImagen> before = historiaImagenRepository.findAllByHistoriaIdOrderByOrdenAsc(historia.getId());

        Long secondImageId = before.get(1).getId();
        String secondStorageKey = before.get(1).getStorageKey();
        Long thirdImageId = before.get(2).getId();

        assertTrue(fileStorageService.exists(secondStorageKey));

        historiaImagenService.deleteImage(historia.getId(), secondImageId);

        List<HistoriaImagen> after = historiaImagenRepository.findAllByHistoriaIdOrderByOrdenAsc(historia.getId());

        assertEquals(2, after.size());
        assertEquals(0, after.get(0).getOrden());
        assertEquals(1, after.get(1).getOrden());
        assertEquals(thirdImageId, after.get(1).getId());
        assertFalse(historiaImagenRepository.existsById(secondImageId));
        assertFalse(fileStorageService.exists(secondStorageKey));
    }

    @Test
    void deleteImageShouldRejectPublishedCover() {
        Historia historia = persistHistoria(EstadoHistoria.PUBLICADA);

        HistoriaImagen portada = metadataImage(UUID.randomUUID() + ".png", 0, true);

        historia.addImagen(portada);
        historiaRepository.saveAndFlush(historia);

        Long historiaId = historia.getId();

        Long imageId = historiaImagenRepository.findAllByHistoriaIdOrderByOrdenAsc(historiaId).get(0).getId();

        assertNotNull(imageId);

        assertThrows(HistoriaServiceException.class, () -> historiaImagenService.deleteImage(historiaId, imageId));

        assertTrue(historiaImagenRepository.existsById(imageId));
    }

    @Test
    void reorderImagesShouldApplyRequestedOrder() {
        Historia historia = persistHistoria(EstadoHistoria.BORRADOR);

        historiaImagenService.addImage(historia.getId(), multipart("primera.png", "image/png", validPng()), null, true);

        historiaImagenService.addImage(historia.getId(), multipart("segunda.png", "image/png", validPng()), null, false);

        historiaImagenService.addImage(historia.getId(), multipart("tercera.png", "image/png", validPng()), null, false);

        List<HistoriaImagen> before = historiaImagenRepository.findAllByHistoriaIdOrderByOrdenAsc(historia.getId());

        Long firstId = before.get(0).getId();
        Long secondId = before.get(1).getId();
        Long thirdId = before.get(2).getId();

        historiaImagenService.reorderImages(historia.getId(), List.of(thirdId, firstId, secondId));

        List<HistoriaImagen> after = historiaImagenRepository.findAllByHistoriaIdOrderByOrdenAsc(historia.getId());

        assertEquals(thirdId, after.get(0).getId());
        assertEquals(firstId, after.get(1).getId());
        assertEquals(secondId, after.get(2).getId());

        assertEquals(0, after.get(0).getOrden());
        assertEquals(1, after.get(1).getOrden());
        assertEquals(2, after.get(2).getOrden());
    }

    @Test
    void reorderImagesShouldRejectDuplicateIds() {
        Historia historia = persistHistoria(EstadoHistoria.BORRADOR);

        historiaImagenService.addImage(historia.getId(), multipart("primera.png", "image/png", validPng()), null, true);

        historiaImagenService.addImage(historia.getId(), multipart("segunda.png", "image/png", validPng()), null, false);

        List<HistoriaImagen> imagenes = historiaImagenRepository.findAllByHistoriaIdOrderByOrdenAsc(historia.getId());

        Long firstId = imagenes.get(0).getId();

        assertThrows(HistoriaServiceException.class, () -> historiaImagenService.reorderImages(historia.getId(), List.of(firstId, firstId))
        );
    }

    @Test
    void reorderImagesShouldRejectImageFromAnotherHistoria() {
        Historia historia1 = persistHistoria(EstadoHistoria.BORRADOR);
        Historia historia2 = persistHistoria(EstadoHistoria.BORRADOR);

        historiaImagenService.addImage(historia1.getId(), multipart("propia.png", "image/png", validPng()), null, true);

        historiaImagenService.addImage(historia2.getId(), multipart("ajena.png", "image/png", validPng()), null, true);

        Long foreignImageId = historiaImagenRepository.findAllByHistoriaIdOrderByOrdenAsc(historia2.getId()).get(0).getId();

        assertThrows(HistoriaServiceException.class, () -> historiaImagenService.reorderImages(historia1.getId(), List.of(foreignImageId)));
    }

    @Test
    void reorderImagesShouldRejectIncompleteList() {
        Historia historia = persistHistoria(EstadoHistoria.BORRADOR);

        historiaImagenService.addImage(historia.getId(), multipart("primera.png", "image/png", validPng()), null, true);

        historiaImagenService.addImage(historia.getId(), multipart("segunda.png", "image/png", validPng()), null, false);

        Long firstId = historiaImagenRepository.findAllByHistoriaIdOrderByOrdenAsc(historia.getId()).get(0).getId();

        assertThrows(HistoriaServiceException.class, () -> historiaImagenService.reorderImages(historia.getId(), List.of(firstId)));
    }

    @Test
    void loadAdminImageShouldAllowDraftHistoriaImage() {
        Historia historia = persistHistoria(EstadoHistoria.BORRADOR);

        historiaImagenService.addImage(historia.getId(), multipart("borrador.png", "image/png", validPng()), null, false);

        HistoriaImagen imagen = historiaImagenRepository.findAllByHistoriaIdOrderByOrdenAsc(historia.getId()).get(0);

        HistoriaImagenService.PublicImage result = historiaImagenService.loadAdminImage(historia.getId(), imagen.getId());

        assertEquals("image/png", result.contentType());
        assertEquals(validPng().length, result.content().length);
    }

    @Test
    void loadAdminImageShouldRejectImageFromAnotherHistoria() {
        Historia historia1 = persistHistoria(EstadoHistoria.BORRADOR);
        Historia historia2 = persistHistoria(EstadoHistoria.BORRADOR);

        historiaImagenService.addImage(historia2.getId(), multipart("ajena.png", "image/png", validPng()), null, false);

        Long foreignImageId = historiaImagenRepository.findAllByHistoriaIdOrderByOrdenAsc(historia2.getId()).get(0).getId();

        HistoriaServiceException exception = assertThrows(HistoriaServiceException.class, () ->
            historiaImagenService.loadAdminImage(historia1.getId(), foreignImageId)
        );

        assertEquals(404, exception.getStatusCode().value());
    }

    @Test
    void loadAdminImageShouldReturnNotFoundWhenPhysicalFileIsMissing() {
        Historia historia = persistHistoria(EstadoHistoria.BORRADOR);

        HistoriaImagen imagen = metadataImage(UUID.randomUUID() + ".png", 0, false);

        historia.addImagen(imagen);
        historiaRepository.saveAndFlush(historia);

        Long imageId = historiaImagenRepository.findAllByHistoriaIdOrderByOrdenAsc(historia.getId()).get(0).getId();

        HistoriaServiceException exception = assertThrows(HistoriaServiceException.class, () ->
            historiaImagenService.loadAdminImage(historia.getId(), imageId)
        );

        assertEquals(404, exception.getStatusCode().value());
    }

    @Test
    void loadPublishedImageShouldReturnNotFoundWhenPhysicalFileIsMissing() {
        Historia historia = persistHistoria(EstadoHistoria.PUBLICADA);

        HistoriaImagen imagen = metadataImage(UUID.randomUUID() + ".png", 0, true);

        historia.addImagen(imagen);
        historiaRepository.saveAndFlush(historia);

        Long imageId = historiaImagenRepository.findAllByHistoriaIdOrderByOrdenAsc(historia.getId()).get(0).getId();

        assertNotNull(imageId);

        HistoriaServiceException exception = assertThrows(HistoriaServiceException.class, () ->
            historiaImagenService.loadPublishedImage(imageId)
        );

        assertEquals(404, exception.getStatusCode().value());
    }

    private Historia persistHistoria(EstadoHistoria estado) {
        Instant now = Instant.now();

        Historia historia = new Historia()
            .titulo("Historia de imágenes")
            .slug("historia-imagenes-" + UUID.randomUUID())
            .resumen("Resumen de prueba")
            .contenido("Contenido de prueba")
            .estado(estado)
            .fechaCreacion(now)
            .fechaActualizacion(now);

        if (estado == EstadoHistoria.PUBLICADA) {
            historia.setFechaPublicacion(now);
        }

        return historiaRepository.saveAndFlush(historia);
    }

    private HistoriaImagen metadataImage(String storageKey, int order, boolean cover) {
        return new HistoriaImagen()
            .storageKey(storageKey)
            .nombreOriginal("imagen-" + order + ".png")
            .contentType("image/png")
            .tamanoBytes(24L)
            .textoAlternativo("Imagen " + order)
            .orden(order)
            .portada(cover);
    }

    private MockMultipartFile multipart(String filename, String contentType, byte[] content) {
        return new MockMultipartFile("file", filename, contentType, content);
    }

    private byte[] validPng() {
        byte[] bytes = new byte[24];

        bytes[0] = (byte) 0x89;
        bytes[1] = 0x50;
        bytes[2] = 0x4E;
        bytes[3] = 0x47;
        bytes[4] = 0x0D;
        bytes[5] = 0x0A;
        bytes[6] = 0x1A;
        bytes[7] = 0x0A;

        bytes[12] = 'I';
        bytes[13] = 'H';
        bytes[14] = 'D';
        bytes[15] = 'R';

        return bytes;
    }

    private byte[] validJpeg() {
        return new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 0x00, (byte) 0xFF, (byte) 0xD9 };
    }

    private byte[] validWebp() {
        byte[] bytes = new byte[16];

        bytes[0] = 'R';
        bytes[1] = 'I';
        bytes[2] = 'F';
        bytes[3] = 'F';

        bytes[8] = 'W';
        bytes[9] = 'E';
        bytes[10] = 'B';
        bytes[11] = 'P';

        bytes[12] = 'V';
        bytes[13] = 'P';
        bytes[14] = '8';
        bytes[15] = ' ';

        return bytes;
    }
}
