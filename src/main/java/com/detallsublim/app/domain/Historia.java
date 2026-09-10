package com.detallsublim.app.domain;

import com.detallsublim.app.domain.enumeration.EstadoHistoria;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * A Historia.
 */
@Entity
@Table(name = "historia")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Historia implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 160)
    @Column(name = "titulo", length = 160, nullable = false)
    private String titulo;

    @NotNull
    @Size(max = 180)
    @Column(name = "slug", length = 180, nullable = false, unique = true)
    private String slug;

    @NotNull
    @Size(max = 300)
    @Column(name = "resumen", length = 300, nullable = false)
    private String resumen;

    @NotNull
    @Size(max = 20000)
    @Lob
    @Column(name = "contenido", nullable = false)
    private String contenido;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 30, nullable = false)
    private EstadoHistoria estado = EstadoHistoria.BORRADOR;

    @NotNull
    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion;

    @NotNull
    @Column(name = "fecha_actualizacion", nullable = false)
    private Instant fechaActualizacion;

    @Column(name = "fecha_publicacion")
    private Instant fechaPublicacion;

    @OneToMany(mappedBy = "historia", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orden ASC")
    @JsonIgnoreProperties(value = { "historia" }, allowSetters = true)
    private List<HistoriaImagen> imagenes = new ArrayList<>();

    public Long getId() {
        return this.id;
    }

    public Historia id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public Historia titulo(String titulo) {
        this.setTitulo(titulo);
        return this;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSlug() {
        return this.slug;
    }

    public Historia slug(String slug) {
        this.setSlug(slug);
        return this;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getResumen() {
        return this.resumen;
    }

    public Historia resumen(String resumen) {
        this.setResumen(resumen);
        return this;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public String getContenido() {
        return this.contenido;
    }

    public Historia contenido(String contenido) {
        this.setContenido(contenido);
        return this;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public EstadoHistoria getEstado() {
        return this.estado;
    }

    public Historia estado(EstadoHistoria estado) {
        this.setEstado(estado);
        return this;
    }

    public void setEstado(EstadoHistoria estado) {
        this.estado = estado;
    }

    public Instant getFechaCreacion() {
        return this.fechaCreacion;
    }

    public Historia fechaCreacion(Instant fechaCreacion) {
        this.setFechaCreacion(fechaCreacion);
        return this;
    }

    public void setFechaCreacion(Instant fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Instant getFechaActualizacion() {
        return this.fechaActualizacion;
    }

    public Historia fechaActualizacion(Instant fechaActualizacion) {
        this.setFechaActualizacion(fechaActualizacion);
        return this;
    }

    public void setFechaActualizacion(Instant fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public Instant getFechaPublicacion() {
        return this.fechaPublicacion;
    }

    public Historia fechaPublicacion(Instant fechaPublicacion) {
        this.setFechaPublicacion(fechaPublicacion);
        return this;
    }

    public void setFechaPublicacion(Instant fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public List<HistoriaImagen> getImagenes() {
        return this.imagenes;
    }

    public void setImagenes(List<HistoriaImagen> imagenes) {
        if (this.imagenes != null) {
            this.imagenes.forEach(imagen -> imagen.setHistoria(null));
        }

        if (imagenes != null) {
            imagenes.forEach(imagen -> imagen.setHistoria(this));
        }

        this.imagenes = imagenes;
    }

    public Historia imagenes(List<HistoriaImagen> imagenes) {
        this.setImagenes(imagenes);
        return this;
    }

    public Historia addImagen(HistoriaImagen imagen) {
        this.imagenes.add(imagen);
        imagen.setHistoria(this);
        return this;
    }

    public Historia removeImagen(HistoriaImagen imagen) {
        this.imagenes.remove(imagen);
        imagen.setHistoria(null);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Historia)) {
            return false;
        }

        return getId() != null && getId().equals(((Historia) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Historia{" +
            "id=" + getId() +
            ", titulo='" + getTitulo() + "'" +
            ", slug='" + getSlug() + "'" +
            ", resumen='" + getResumen() + "'" +
            ", estado='" + getEstado() + "'" +
            ", fechaCreacion='" + getFechaCreacion() + "'" +
            ", fechaActualizacion='" + getFechaActualizacion() + "'" +
            ", fechaPublicacion='" + getFechaPublicacion() + "'" +
            "}";
    }
}
