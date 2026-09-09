package com.detallsublim.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * Una imagen asociada a una Historia.
 */
@Entity
@Table(name = "historia_imagen")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class HistoriaImagen implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(name = "storage_key", length = 255, nullable = false, unique = true)
    private String storageKey;

    @NotNull
    @Size(max = 255)
    @Column(name = "nombre_original", length = 255, nullable = false)
    private String nombreOriginal;

    @NotNull
    @Size(max = 100)
    @Column(name = "content_type", length = 100, nullable = false)
    private String contentType;

    @NotNull
    @Min(0)
    @Column(name = "tamano_bytes", nullable = false)
    private Long tamanoBytes;

    @Size(max = 160)
    @Column(name = "texto_alternativo", length = 160)
    private String textoAlternativo;

    @NotNull
    @Min(0)
    @Column(name = "orden", nullable = false)
    private Integer orden;

    @NotNull
    @Column(name = "portada", nullable = false)
    private Boolean portada = false;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "historia_id", nullable = false)
    @JsonIgnoreProperties(value = { "imagenes" }, allowSetters = true)
    private Historia historia;

    public Long getId() {
        return this.id;
    }

    public HistoriaImagen id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStorageKey() {
        return this.storageKey;
    }

    public HistoriaImagen storageKey(String storageKey) {
        this.setStorageKey(storageKey);
        return this;
    }

    public void setStorageKey(String storageKey) {
        this.storageKey = storageKey;
    }

    public String getNombreOriginal() {
        return this.nombreOriginal;
    }

    public HistoriaImagen nombreOriginal(String nombreOriginal) {
        this.setNombreOriginal(nombreOriginal);
        return this;
    }

    public void setNombreOriginal(String nombreOriginal) {
        this.nombreOriginal = nombreOriginal;
    }

    public String getContentType() {
        return this.contentType;
    }

    public HistoriaImagen contentType(String contentType) {
        this.setContentType(contentType);
        return this;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getTamanoBytes() {
        return this.tamanoBytes;
    }

    public HistoriaImagen tamanoBytes(Long tamanoBytes) {
        this.setTamanoBytes(tamanoBytes);
        return this;
    }

    public void setTamanoBytes(Long tamanoBytes) {
        this.tamanoBytes = tamanoBytes;
    }

    public String getTextoAlternativo() {
        return this.textoAlternativo;
    }

    public HistoriaImagen textoAlternativo(String textoAlternativo) {
        this.setTextoAlternativo(textoAlternativo);
        return this;
    }

    public void setTextoAlternativo(String textoAlternativo) {
        this.textoAlternativo = textoAlternativo;
    }

    public Integer getOrden() {
        return this.orden;
    }

    public HistoriaImagen orden(Integer orden) {
        this.setOrden(orden);
        return this;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public Boolean getPortada() {
        return this.portada;
    }

    public HistoriaImagen portada(Boolean portada) {
        this.setPortada(portada);
        return this;
    }

    public void setPortada(Boolean portada) {
        this.portada = portada;
    }

    public Historia getHistoria() {
        return this.historia;
    }

    public HistoriaImagen historia(Historia historia) {
        this.setHistoria(historia);
        return this;
    }

    public void setHistoria(Historia historia) {
        this.historia = historia;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof HistoriaImagen)) {
            return false;
        }

        return getId() != null && getId().equals(((HistoriaImagen) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HistoriaImagen{" +
            "id=" + getId() +
            ", storageKey='" + getStorageKey() + "'" +
            ", nombreOriginal='" + getNombreOriginal() + "'" +
            ", contentType='" + getContentType() + "'" +
            ", tamanoBytes=" + getTamanoBytes() +
            ", textoAlternativo='" + getTextoAlternativo() + "'" +
            ", orden=" + getOrden() +
            ", portada='" + getPortada() + "'" +
            "}";
    }
}
