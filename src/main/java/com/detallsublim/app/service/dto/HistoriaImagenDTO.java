package com.detallsublim.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * DTO para una imagen asociada a una Historia.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class HistoriaImagenDTO implements Serializable {

    private Long id;

    @Size(max = 160)
    private String textoAlternativo;

    @NotNull
    @Min(0)
    private Integer orden;

    @NotNull
    private Boolean portada;

    private String url;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTextoAlternativo() {
        return textoAlternativo;
    }

    public void setTextoAlternativo(String textoAlternativo) {
        this.textoAlternativo = textoAlternativo;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public Boolean getPortada() {
        return portada;
    }

    public void setPortada(Boolean portada) {
        this.portada = portada;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof HistoriaImagenDTO)) {
            return false;
        }

        HistoriaImagenDTO historiaImagenDTO = (HistoriaImagenDTO) o;

        if (this.id == null) {
            return false;
        }

        return Objects.equals(this.id, historiaImagenDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HistoriaImagenDTO{" +
            "id=" + getId() +
            ", textoAlternativo='" + getTextoAlternativo() + "'" +
            ", orden=" + getOrden() +
            ", portada='" + getPortada() + "'" +
            ", url='" + getUrl() + "'" +
            "}";
    }
}
