package com.detallsublim.app.web.rest;

import com.detallsublim.app.service.HistoriaImagenService;
import com.detallsublim.app.service.HistoriaService;
import com.detallsublim.app.service.dto.HistoriaDTO;
import com.detallsublim.app.service.dto.HistoriaImagenDTO;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * Administración de Historias.
 *
 * Todas las operaciones requieren ROLE_ADMIN.
 */
@RestController
@RequestMapping("/api/admin/historias")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminHistoriaResource {

    private static final Logger LOG = LoggerFactory.getLogger(AdminHistoriaResource.class);

    private final HistoriaService historiaService;

    public AdminHistoriaResource(HistoriaService historiaService, HistoriaImagenService historiaImagenService) {
        this.historiaService = historiaService;
        this.historiaImagenService = historiaImagenService;
    }

    private final HistoriaImagenService historiaImagenService;

    /**
     * Crea una Historia nueva.
     *
     * El backend fuerza BORRADOR, slug y fechas.
     */
    @PostMapping("")
    public ResponseEntity<HistoriaDTO> createHistoria(@Valid @RequestBody HistoriaDTO historiaDTO) throws URISyntaxException {
        LOG.debug("REST request to create Historia");

        if (historiaDTO.getId() != null) {
            throw new IllegalArgumentException("Una Historia nueva no puede tener id.");
        }

        HistoriaDTO result = historiaService.create(historiaDTO);

        return ResponseEntity.created(new URI("/api/admin/historias/" + result.getId())).body(result);
    }

    /**
     * Actualiza únicamente los campos editables de una Historia.
     */
    @PutMapping("/{id}")
    public ResponseEntity<HistoriaDTO> updateHistoria(@PathVariable("id") Long id, @Valid @RequestBody HistoriaDTO historiaDTO) {
        LOG.debug("REST request to update Historia : {}", id);

        if (historiaDTO.getId() != null && !Objects.equals(id, historiaDTO.getId())) {
            throw new IllegalArgumentException("El id de la ruta no coincide con el de la Historia.");
        }

        return ResponseEntity.ok(historiaService.update(id, historiaDTO));
    }

    /**
     * Lista todas las Historias para administración,
     * incluyendo borradores y publicadas.
     */
    @GetMapping("")
    public ResponseEntity<List<HistoriaDTO>> getHistorias(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get admin Historias");

        Page<HistoriaDTO> page = historiaService.findAllForAdmin(pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * Obtiene una Historia por id para administración.
     */
    @GetMapping("/{id}")
    public ResponseEntity<HistoriaDTO> getHistoria(@PathVariable("id") Long id) {
        LOG.debug("REST request to get admin Historia : {}", id);

        return ResponseUtil.wrapOrNotFound(historiaService.findOneForAdmin(id));
    }

    /**
     * Publica una Historia.
     */
    @PostMapping("/{id}/publicar")
    public ResponseEntity<HistoriaDTO> publishHistoria(@PathVariable("id") Long id) {
        LOG.debug("REST request to publish Historia : {}", id);

        return ResponseEntity.ok(historiaService.publish(id));
    }

    /**
     * Retira una Historia de la zona pública.
     */
    @PostMapping("/{id}/despublicar")
    public ResponseEntity<HistoriaDTO> unpublishHistoria(@PathVariable("id") Long id) {
        LOG.debug("REST request to unpublish Historia : {}", id);

        return ResponseEntity.ok(historiaService.unpublish(id));
    }

    /**
     * Añade una imagen a una Historia.
     */
    @PostMapping(value = "/{id}/imagenes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HistoriaImagenDTO> addImagen(
        @PathVariable("id") Long id,
        @RequestParam("file") MultipartFile file,
        @RequestParam(name = "textoAlternativo", required = false) String textoAlternativo,
        @RequestParam(name = "portada", defaultValue = "false") boolean portada
    ) {
        LOG.debug("REST request to add image to Historia : {}", id);

        HistoriaImagenDTO result = historiaImagenService.addImage(id, file, textoAlternativo, portada);

        return ResponseEntity.ok(result);
    }

    /**
     * Elimina completamente una Historia.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHistoria(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Historia : {}", id);

        historiaService.delete(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Elimina una imagen concreta de una Historia.
     */
    @DeleteMapping("/{id}/imagenes/{imagenId}")
    public ResponseEntity<Void> deleteImagen(@PathVariable("id") Long id, @PathVariable("imagenId") Long imagenId) {
        LOG.debug("REST request to delete image {} from Historia {}", imagenId, id);

        historiaImagenService.deleteImage(id, imagenId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Establece una imagen como portada de la Historia.
     */
    @PostMapping("/{id}/imagenes/{imagenId}/portada")
    public ResponseEntity<HistoriaImagenDTO> setPortada(@PathVariable("id") Long id, @PathVariable("imagenId") Long imagenId) {
        LOG.debug("REST request to set image {} as cover for Historia {}", imagenId, id);

        HistoriaImagenDTO result = historiaImagenService.setCover(id, imagenId);

        return ResponseEntity.ok(result);
    }

    /**
     * Reordena todas las imágenes de una Historia.
     *
     * El cuerpo debe contener exactamente todos los ids actuales
     * en el orden visual deseado.
     */
    @PutMapping("/{id}/imagenes/orden")
    public ResponseEntity<Void> reorderImagenes(@PathVariable("id") Long id, @RequestBody List<Long> imageIds) {
        LOG.debug("REST request to reorder images for Historia : {}", id);

        historiaImagenService.reorderImages(id, imageIds);

        return ResponseEntity.noContent().build();
    }
}
