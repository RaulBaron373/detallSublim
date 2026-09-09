package com.detallsublim.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.detallsublim.app.IntegrationTest;
import com.detallsublim.app.domain.Historia;
import com.detallsublim.app.domain.HistoriaImagen;
import com.detallsublim.app.domain.enumeration.EstadoHistoria;
import com.detallsublim.app.repository.HistoriaRepository;
import com.detallsublim.app.service.dto.HistoriaDTO;
import com.detallsublim.app.service.storage.FileStorageService;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class HistoriaServiceIT {

    @Autowired
    private HistoriaService historiaService;

    @Autowired
    private HistoriaRepository historiaRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @AfterEach
    void cleanup() {
        historiaRepository.deleteAll();
        historiaRepository.flush();
    }

    @Test
    void createShouldAlwaysCreateDraftAndGenerateServerFields() {
        HistoriaDTO input = validDto();
        input.setEstado(EstadoHistoria.PUBLICADA);
        input.setSlug("slug-enviado-por-cliente");
        input.setFechaCreacion(Instant.EPOCH);
        input.setFechaActualizacion(Instant.EPOCH);
        input.setFechaPublicacion(Instant.EPOCH);

        HistoriaDTO result = historiaService.create(input);

        assertNotNull(result.getId());
        assertEquals(EstadoHistoria.BORRADOR, result.getEstado());
        assertEquals("historia-de-prueba", result.getSlug());
        assertNotNull(result.getFechaCreacion());
        assertNotNull(result.getFechaActualizacion());
        assertNull(result.getFechaPublicacion());
    }

    @Test
    void createShouldTrimEditableFields() {
        HistoriaDTO input = new HistoriaDTO();
        input.setTitulo("   Historia con espacios   ");
        input.setResumen("   Resumen con espacios   ");
        input.setContenido("   Contenido con espacios   ");

        HistoriaDTO result = historiaService.create(input);

        assertEquals("Historia con espacios", result.getTitulo());
        assertEquals("Resumen con espacios", result.getResumen());
        assertEquals("Contenido con espacios", result.getContenido());
    }

    @Test
    void createShouldGenerateNormalizedSlug() {
        HistoriaDTO input = validDto();
        input.setTitulo("Café, Diseño y Celebración");

        HistoriaDTO result = historiaService.create(input);

        assertEquals("cafe-diseno-y-celebracion", result.getSlug());
    }

    @Test
    void createShouldGenerateUniqueSlugWhenSlugAlreadyExists() {
        HistoriaDTO first = validDto();
        first.setTitulo("Historia repetida");

        HistoriaDTO second = validDto();
        second.setTitulo("Historia repetida");

        HistoriaDTO firstResult = historiaService.create(first);
        HistoriaDTO secondResult = historiaService.create(second);

        assertEquals("historia-repetida", firstResult.getSlug());
        assertEquals("historia-repetida-2", secondResult.getSlug());
    }

    @Test
    void updateShouldKeepSlugStableWhenTitleChanges() {
        HistoriaDTO created = historiaService.create(validDto());
        String originalSlug = created.getSlug();

        HistoriaDTO update = validDto();
        update.setTitulo("Título completamente diferente");

        HistoriaDTO updated = historiaService.update(created.getId(), update);

        assertEquals(originalSlug, updated.getSlug());
        assertEquals("Título completamente diferente", updated.getTitulo());
    }

    @Test
    void createShouldRejectMissingTitle() {
        HistoriaDTO input = validDto();
        input.setTitulo("   ");

        assertThrows(HistoriaServiceException.class, () -> historiaService.create(input));
    }

    @Test
    void createShouldRejectMissingSummary() {
        HistoriaDTO input = validDto();
        input.setResumen(null);

        assertThrows(HistoriaServiceException.class, () -> historiaService.create(input));
    }

    @Test
    void createShouldRejectMissingContent() {
        HistoriaDTO input = validDto();
        input.setContenido("");

        assertThrows(HistoriaServiceException.class, () -> historiaService.create(input));
    }

    @Test
    void publishShouldRejectHistoriaWithoutCover() {
        HistoriaDTO created = historiaService.create(validDto());

        assertThrows(HistoriaServiceException.class, () -> historiaService.publish(created.getId()));

        Historia persisted = historiaRepository.findById(created.getId()).orElseThrow();

        assertEquals(EstadoHistoria.BORRADOR, persisted.getEstado());
        assertNull(persisted.getFechaPublicacion());
    }

    @Test
    void publishShouldRejectHistoriaWithMoreThanOneCover() {
        Historia historia = persistHistoriaWithCover(EstadoHistoria.BORRADOR);

        HistoriaImagen secondCover = new HistoriaImagen()
            .storageKey("test-" + UUID.randomUUID() + ".png")
            .nombreOriginal("segunda-portada.png")
            .contentType("image/png")
            .tamanoBytes(4L)
            .textoAlternativo("Segunda portada")
            .orden(1)
            .portada(true);

        historia.addImagen(secondCover);
        historia = historiaRepository.saveAndFlush(historia);

        Long historiaId = historia.getId();

        assertThrows(HistoriaServiceException.class, () -> historiaService.publish(historiaId));
    }

    @Test
    void publishShouldPublishHistoriaWithExactlyOneCover() {
        Historia historia = persistHistoriaWithCover(EstadoHistoria.BORRADOR);

        HistoriaDTO result = historiaService.publish(historia.getId());

        assertEquals(EstadoHistoria.PUBLICADA, result.getEstado());
        assertNotNull(result.getFechaPublicacion());
        assertNotNull(result.getFechaActualizacion());
    }

    @Test
    void publishShouldPreserveFirstPublicationDate() throws InterruptedException {
        Historia historia = persistHistoriaWithCover(EstadoHistoria.BORRADOR);
        Long historiaId = historia.getId();

        historiaService.publish(historiaId);

        Instant firstPublicationDate = historiaRepository.findById(historiaId).orElseThrow().getFechaPublicacion();

        assertNotNull(firstPublicationDate);

        historiaService.unpublish(historiaId);

        Thread.sleep(5);

        historiaService.publish(historiaId);

        Instant secondPublicationDate = historiaRepository.findById(historiaId).orElseThrow().getFechaPublicacion();

        assertEquals(firstPublicationDate, secondPublicationDate);
    }

    @Test
    void unpublishShouldReturnHistoriaToDraftAndPreservePublicationDate() {
        Historia historia = persistHistoriaWithCover(EstadoHistoria.BORRADOR);
        Long historiaId = historia.getId();

        historiaService.publish(historiaId);

        Instant publicationDate = historiaRepository.findById(historiaId).orElseThrow().getFechaPublicacion();

        assertNotNull(publicationDate);

        historiaService.unpublish(historiaId);

        Historia persisted = historiaRepository.findById(historiaId).orElseThrow();

        assertEquals(EstadoHistoria.BORRADOR, persisted.getEstado());
        assertEquals(publicationDate, persisted.getFechaPublicacion());
    }

    @Test
    void deleteShouldDeleteHistoriaAndPhysicalFiles() {
        byte[] content = new byte[] { 1, 2, 3, 4 };
        String storageKey = fileStorageService.store(content, "png");

        assertTrue(fileStorageService.exists(storageKey));

        Instant now = Instant.now();

        Historia historia = new Historia()
            .titulo("Historia para borrar")
            .slug("historia-para-borrar-" + UUID.randomUUID())
            .resumen("Resumen")
            .contenido("Contenido")
            .estado(EstadoHistoria.BORRADOR)
            .fechaCreacion(now)
            .fechaActualizacion(now);

        HistoriaImagen imagen = new HistoriaImagen()
            .storageKey(storageKey)
            .nombreOriginal("imagen.png")
            .contentType("image/png")
            .tamanoBytes((long) content.length)
            .textoAlternativo("Imagen")
            .orden(0)
            .portada(true);

        historia.addImagen(imagen);
        historia = historiaRepository.saveAndFlush(historia);

        Long historiaId = historia.getId();

        historiaService.delete(historiaId);

        assertFalse(historiaRepository.existsById(historiaId));
        assertFalse(fileStorageService.exists(storageKey));
    }

    private HistoriaDTO validDto() {
        HistoriaDTO dto = new HistoriaDTO();
        dto.setTitulo("Historia de prueba");
        dto.setResumen("Resumen de prueba");
        dto.setContenido("Contenido de prueba");
        return dto;
    }

    private Historia persistHistoriaWithCover(EstadoHistoria estado) {
        Instant now = Instant.now();

        Historia historia = new Historia()
            .titulo("Historia con portada")
            .slug("historia-con-portada-" + UUID.randomUUID())
            .resumen("Resumen de prueba")
            .contenido("Contenido de prueba")
            .estado(estado)
            .fechaCreacion(now)
            .fechaActualizacion(now);

        HistoriaImagen imagen = new HistoriaImagen()
            .storageKey("test-" + UUID.randomUUID() + ".png")
            .nombreOriginal("portada.png")
            .contentType("image/png")
            .tamanoBytes(4L)
            .textoAlternativo("Portada de prueba")
            .orden(0)
            .portada(true);

        historia.addImagen(imagen);

        return historiaRepository.saveAndFlush(historia);
    }
}
