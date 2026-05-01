package com.detallsublim.app.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.detallsublim.app.domain.Producto} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductoDTO implements Serializable {

    private Long id;

    @NotNull
    private String nombre;

    @Lob
    private String descripcion;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal precioBase;

    @NotNull
    private Boolean personalizable;

    private Integer plazoEstimadoDias;

    private String imagenUrl;

    @NotNull
    private Boolean activo;

    private CategoriaDTO categoria;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(BigDecimal precioBase) {
        this.precioBase = precioBase;
    }

    public Boolean getPersonalizable() {
        return personalizable;
    }

    public void setPersonalizable(Boolean personalizable) {
        this.personalizable = personalizable;
    }

    public Integer getPlazoEstimadoDias() {
        return plazoEstimadoDias;
    }

    public void setPlazoEstimadoDias(Integer plazoEstimadoDias) {
        this.plazoEstimadoDias = plazoEstimadoDias;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public CategoriaDTO getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaDTO categoria) {
        this.categoria = categoria;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductoDTO)) {
            return false;
        }

        ProductoDTO productoDTO = (ProductoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductoDTO{" +
            "id=" + getId() +
            ", nombre='" + getNombre() + "'" +
            ", descripcion='" + getDescripcion() + "'" +
            ", precioBase=" + getPrecioBase() +
            ", personalizable='" + getPersonalizable() + "'" +
            ", plazoEstimadoDias=" + getPlazoEstimadoDias() +
            ", imagenUrl='" + getImagenUrl() + "'" +
            ", activo='" + getActivo() + "'" +
            ", categoria=" + getCategoria() +
            "}";
    }
}
