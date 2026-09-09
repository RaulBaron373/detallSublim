package com.detallsublim.app.web.rest;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.detallsublim.app.IntegrationTest;
import com.detallsublim.app.domain.Historia;
import com.detallsublim.app.domain.HistoriaImagen;
import com.detallsublim.app.domain.enumeration.EstadoHistoria;
import com.detallsublim.app.repository.HistoriaRepository;
import com.detallsublim.app.security.AuthoritiesConstants;
import com.detallsublim.app.service.storage.FileStorageService;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@IntegrationTest
class HistoriaSecurityIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HistoriaRepository historiaRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Test
    @WithUnauthenticatedMockUser
    void publicHistoriasShouldBeAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/public/historias")).andExpect(status().isOk());
    }

    @Test
    @WithUnauthenticatedMockUser
    void publicUnknownHistoriaShouldReturnNotFoundInsteadOfUnauthorized() throws Exception {
        mockMvc.perform(get("/api/public/historias/historia-que-no-existe")).andExpect(status().isNotFound());
    }

    @Test
    @WithUnauthenticatedMockUser
    void publishedHistoriaShouldBeAccessiblePublicly() throws Exception {
        Instant now = Instant.now();

        Historia historia = new Historia()
            .titulo("Historia publicada")
            .slug("historia-publicada-security-test")
            .resumen("Resumen de prueba")
            .contenido("Contenido de prueba")
            .estado(EstadoHistoria.PUBLICADA)
            .fechaCreacion(now)
            .fechaActualizacion(now)
            .fechaPublicacion(now);

        historia = historiaRepository.saveAndFlush(historia);

        try {
            mockMvc.perform(get("/api/public/historias/" + historia.getSlug())).andExpect(status().isOk());
        } finally {
            historiaRepository.deleteById(historia.getId());
            historiaRepository.flush();
        }
    }

    @Test
    @WithUnauthenticatedMockUser
    void draftHistoriaShouldNotBeAccessiblePublicly() throws Exception {
        Instant now = Instant.now();

        Historia historia = new Historia()
            .titulo("Historia borrador")
            .slug("historia-borrador-security-test")
            .resumen("Resumen de prueba")
            .contenido("Contenido de prueba")
            .estado(EstadoHistoria.BORRADOR)
            .fechaCreacion(now)
            .fechaActualizacion(now);

        historia = historiaRepository.saveAndFlush(historia);

        try {
            mockMvc.perform(get("/api/public/historias/" + historia.getSlug())).andExpect(status().isNotFound());
        } finally {
            historiaRepository.deleteById(historia.getId());
            historiaRepository.flush();
        }
    }

    @Test
    @WithUnauthenticatedMockUser
    void publishedHistoriaImageShouldBeAccessiblePublicly() throws Exception {
        Instant now = Instant.now();
        byte[] imageContent = new byte[] { 1, 2, 3, 4 };
        String storageKey = fileStorageService.store(imageContent, "png");

        Historia historia = new Historia()
            .titulo("Historia publicada con imagen")
            .slug("historia-publicada-imagen-security-test")
            .resumen("Resumen de prueba")
            .contenido("Contenido de prueba")
            .estado(EstadoHistoria.PUBLICADA)
            .fechaCreacion(now)
            .fechaActualizacion(now)
            .fechaPublicacion(now);

        HistoriaImagen imagen = new HistoriaImagen()
            .storageKey(storageKey)
            .nombreOriginal("security-test.png")
            .contentType("image/png")
            .tamanoBytes((long) imageContent.length)
            .textoAlternativo("Imagen de prueba")
            .orden(0)
            .portada(true);

        historia.addImagen(imagen);
        historia = historiaRepository.saveAndFlush(historia);

        try {
            mockMvc
                .perform(get("/api/public/historias/imagenes/" + imagen.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/png"))
                .andExpect(content().bytes(imageContent));
        } finally {
            try {
                historiaRepository.deleteById(historia.getId());
                historiaRepository.flush();
            } finally {
                fileStorageService.delete(storageKey);
            }
        }
    }

    @Test
    @WithUnauthenticatedMockUser
    void draftHistoriaImageShouldNotBeAccessiblePublicly() throws Exception {
        Instant now = Instant.now();

        Historia historia = new Historia()
            .titulo("Historia borrador con imagen")
            .slug("historia-borrador-imagen-security-test")
            .resumen("Resumen de prueba")
            .contenido("Contenido de prueba")
            .estado(EstadoHistoria.BORRADOR)
            .fechaCreacion(now)
            .fechaActualizacion(now);

        HistoriaImagen imagen = new HistoriaImagen()
            .storageKey("security-draft-" + UUID.randomUUID() + ".png")
            .nombreOriginal("security-draft.png")
            .contentType("image/png")
            .tamanoBytes(4L)
            .textoAlternativo("Imagen privada de prueba")
            .orden(0)
            .portada(true);

        historia.addImagen(imagen);
        historia = historiaRepository.saveAndFlush(historia);

        try {
            mockMvc.perform(get("/api/public/historias/imagenes/" + imagen.getId())).andExpect(status().isNotFound());
        } finally {
            historiaRepository.deleteById(historia.getId());
            historiaRepository.flush();
        }
    }

    @Test
    @WithUnauthenticatedMockUser
    void anonymousUserShouldNotAccessAdminHistorias() throws Exception {
        mockMvc.perform(get("/api/admin/historias")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = AuthoritiesConstants.USER)
    void normalUserShouldNotAccessAdminHistorias() throws Exception {
        mockMvc.perform(get("/api/admin/historias")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    void adminShouldAccessAdminHistorias() throws Exception {
        mockMvc.perform(get("/api/admin/historias")).andExpect(status().isOk());
    }

    @Test
    @WithUnauthenticatedMockUser
    void publicHistoriasShouldRejectAnonymousPost() throws Exception {
        mockMvc.perform(post("/api/public/historias")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithUnauthenticatedMockUser
    void publicHistoriasShouldRejectAnonymousPut() throws Exception {
        mockMvc.perform(put("/api/public/historias/1")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithUnauthenticatedMockUser
    void publicHistoriasShouldRejectAnonymousDelete() throws Exception {
        mockMvc.perform(delete("/api/public/historias/1")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithUnauthenticatedMockUser
    void publicHistoriasShouldReturnLightweightSummaryWithoutContentOrImageCollection() throws Exception {
        Instant now = Instant.now();
        String slug = "historia-resumen-publico-security-test";

        Historia historia = new Historia()
            .titulo("Historia para listado público")
            .slug(slug)
            .resumen("Resumen visible en el listado")
            .contenido("Este contenido completo no debe aparecer en el listado")
            .estado(EstadoHistoria.PUBLICADA)
            .fechaCreacion(now)
            .fechaActualizacion(now)
            .fechaPublicacion(now);

        HistoriaImagen portada = new HistoriaImagen()
            .storageKey(UUID.randomUUID() + ".png")
            .nombreOriginal("portada-listado.png")
            .contentType("image/png")
            .tamanoBytes(4L)
            .textoAlternativo("Portada del listado")
            .orden(0)
            .portada(true);

        historia.addImagen(portada);
        historia = historiaRepository.saveAndFlush(historia);

        try {
            mockMvc
                .perform(get("/api/public/historias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].slug").value(hasItem(slug)))
                .andExpect(
                    jsonPath("$[?(@.slug == '" + slug + "')].portadaUrl").value(
                        hasItem("/api/public/historias/imagenes/" + portada.getId())
                    )
                )
                .andExpect(jsonPath("$[?(@.slug == '" + slug + "')].contenido").doesNotExist())
                .andExpect(jsonPath("$[?(@.slug == '" + slug + "')].imagenes").doesNotExist());
        } finally {
            historiaRepository.deleteById(historia.getId());
            historiaRepository.flush();
        }
    }
}
