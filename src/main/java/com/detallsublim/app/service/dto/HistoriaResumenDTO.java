package com.detallsublim.app.service.dto;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO ligero para el listado público de Historias.
 *
 * No incluye el contenido completo ni la colección de imágenes.
 * Únicamente expone la portada necesaria para representar la Historia.
 */
public class HistoriaResumenDTO implements Serializable {

    private Long id;
    private String titulo;
    private String slug;
    private String resumen;
    private Instant fechaPublicacion;
    private Long portadaId;
    private String portadaTextoAlternativo;
    private String portadaUrl;

    public HistoriaResumenDTO(
        Long id,
        String titulo,
        String slug,
        String resumen,
        Instant fechaPublicacion,
        Long portadaId,
        String portadaTextoAlternativo
    ) {
        this.id = id;
        this.titulo = titulo;
        this.slug = slug;
        this.resumen = resumen;
        this.fechaPublicacion = fechaPublicacion;
        this.portadaId = portadaId;
        this.portadaTextoAlternativo = portadaTextoAlternativo;
        this.portadaUrl = portadaId != null ? "/api/public/historias/imagenes/" + portadaId : null;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getSlug() {
        return slug;
    }

    public String getResumen() {
        return resumen;
    }

    public Instant getFechaPublicacion() {
        return fechaPublicacion;
    }

    public Long getPortadaId() {
        return portadaId;
    }

    public String getPortadaTextoAlternativo() {
        return portadaTextoAlternativo;
    }

    public String getPortadaUrl() {
        return portadaUrl;
    }
}
