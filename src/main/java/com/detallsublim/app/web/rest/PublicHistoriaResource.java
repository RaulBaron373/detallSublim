package com.detallsublim.app.web.rest;

import com.detallsublim.app.service.HistoriaImagenService;
import com.detallsublim.app.service.HistoriaImagenService.PublicImage;
import com.detallsublim.app.service.HistoriaService;
import com.detallsublim.app.service.dto.HistoriaDTO;
import com.detallsublim.app.service.dto.HistoriaResumenDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * Endpoints públicos de Historias.
 *
 * Solo permiten consultar Historias publicadas y sus imágenes.
 */
@RestController
@RequestMapping("/api/public/historias")
public class PublicHistoriaResource {

    private static final Logger LOG = LoggerFactory.getLogger(PublicHistoriaResource.class);

    private final HistoriaService historiaService;
    private final HistoriaImagenService historiaImagenService;

    public PublicHistoriaResource(HistoriaService historiaService, HistoriaImagenService historiaImagenService) {
        this.historiaService = historiaService;
        this.historiaImagenService = historiaImagenService;
    }

    /**
     * Obtiene una página de Historias publicadas,
     * ordenadas por fecha de publicación descendente.
     */
    @GetMapping("")
    public ResponseEntity<List<HistoriaResumenDTO>> getHistorias(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get published Historias");

        Page<HistoriaResumenDTO> page = historiaService.findPublished(pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * Obtiene una Historia publicada por su slug.
     */
    @GetMapping("/{slug}")
    public ResponseEntity<HistoriaDTO> getHistoria(@PathVariable("slug") String slug) {
        LOG.debug("REST request to get published Historia by slug");

        return ResponseUtil.wrapOrNotFound(historiaService.findPublishedBySlug(slug));
    }

    /**
     * Sirve una imagen únicamente si pertenece a una Historia publicada.
     */
    @GetMapping("/imagenes/{id}")
    public ResponseEntity<byte[]> getImagen(@PathVariable("id") Long id) {
        LOG.debug("REST request to get published Historia image : {}", id);

        PublicImage image = historiaImagenService.loadPublishedImage(id);

        MediaType mediaType = MediaType.parseMediaType(image.contentType());

        return ResponseEntity.ok()
            .contentType(mediaType)
            .cacheControl(CacheControl.maxAge(java.time.Duration.ofDays(30)).cachePublic())
            .body(image.content());
    }
}
