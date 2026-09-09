package com.detallsublim.app.service.dto;

import com.detallsublim.app.domain.enumeration.EstadoHistoria;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * DTO para una Historia.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class HistoriaDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 160)
    private String titulo;

    private String slug;

    @NotNull
    @Size(max = 300)
    private String resumen;

    @NotNull
    @Size(max = 20000)
    private String contenido;

    private EstadoHistoria estado;

    private Instant fechaCreacion;

    private Instant fechaActualizacion;

    private Instant fechaPublicacion;

    private List<HistoriaImagenDTO> imagenes = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public EstadoHistoria getEstado() {
        return estado;
    }

    public void setEstado(EstadoHistoria estado) {
        this.estado = estado;
    }

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Instant fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Instant getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(Instant fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public Instant getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(Instant fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public List<HistoriaImagenDTO> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<HistoriaImagenDTO> imagenes) {
        this.imagenes = imagenes != null ? imagenes : new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof HistoriaDTO)) {
            return false;
        }

        HistoriaDTO historiaDTO = (HistoriaDTO) o;

        if (this.id == null) {
            return false;
        }

        return Objects.equals(this.id, historiaDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HistoriaDTO{" +
            "id=" + getId() +
            ", titulo='" + getTitulo() + "'" +
            ", slug='" + getSlug() + "'" +
            ", estado='" + getEstado() + "'" +
            ", fechaCreacion='" + getFechaCreacion() + "'" +
            ", fechaActualizacion='" + getFechaActualizacion() + "'" +
            ", fechaPublicacion='" + getFechaPublicacion() + "'" +
            ", numeroImagenes=" + (getImagenes() != null ? getImagenes().size() : 0) +
            "}";
    }
}
