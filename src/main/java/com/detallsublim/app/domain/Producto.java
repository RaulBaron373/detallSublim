package com.detallsublim.app.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A Producto.
 */
@Entity
@Table(name = "producto")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Producto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Lob
    @Column(name = "descripcion")
    private String descripcion;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "precio_base", precision = 21, scale = 2, nullable = false)
    private BigDecimal precioBase;

    @NotNull
    @Column(name = "personalizable", nullable = false)
    private Boolean personalizable;

    @Column(name = "plazo_estimado_dias")
    private Integer plazoEstimadoDias;

    @Lob
    @Column(name = "imagen_url", columnDefinition = "LONGTEXT")
    private String imagenUrl;

    @NotNull
    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @NotNull
    @Column(name = "destacado", nullable = false)
    private Boolean destacado;

    @ManyToOne(fetch = FetchType.LAZY)
    private Categoria categoria;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Producto id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public Producto nombre(String nombre) {
        this.setNombre(nombre);
        return this;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public Producto descripcion(String descripcion) {
        this.setDescripcion(descripcion);
        return this;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecioBase() {
        return this.precioBase;
    }

    public Producto precioBase(BigDecimal precioBase) {
        this.setPrecioBase(precioBase);
        return this;
    }

    public void setPrecioBase(BigDecimal precioBase) {
        this.precioBase = precioBase;
    }

    public Boolean getPersonalizable() {
        return this.personalizable;
    }

    public Producto personalizable(Boolean personalizable) {
        this.setPersonalizable(personalizable);
        return this;
    }

    public void setPersonalizable(Boolean personalizable) {
        this.personalizable = personalizable;
    }

    public Integer getPlazoEstimadoDias() {
        return this.plazoEstimadoDias;
    }

    public Producto plazoEstimadoDias(Integer plazoEstimadoDias) {
        this.setPlazoEstimadoDias(plazoEstimadoDias);
        return this;
    }

    public void setPlazoEstimadoDias(Integer plazoEstimadoDias) {
        this.plazoEstimadoDias = plazoEstimadoDias;
    }

    public String getImagenUrl() {
        return this.imagenUrl;
    }

    public Producto imagenUrl(String imagenUrl) {
        this.setImagenUrl(imagenUrl);
        return this;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public Boolean getActivo() {
        return this.activo;
    }

    public Producto activo(Boolean activo) {
        this.setActivo(activo);
        return this;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Categoria getCategoria() {
        return this.categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Producto categoria(Categoria categoria) {
        this.setCategoria(categoria);
        return this;
    }

    public Boolean getDestacado() {
        return this.destacado;
    }

    public Producto destacado(Boolean destacado) {
        this.setDestacado(destacado);
        return this;
    }

    public void setDestacado(Boolean destacado) {
        this.destacado = destacado;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Producto)) {
            return false;
        }
        return getId() != null && getId().equals(((Producto) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Producto{" +
            "id=" + getId() +
            ", nombre='" + getNombre() + "'" +
            ", descripcion='" + getDescripcion() + "'" +
            ", precioBase=" + getPrecioBase() +
            ", personalizable='" + getPersonalizable() + "'" +
            ", plazoEstimadoDias=" + getPlazoEstimadoDias() +
            ", imagenUrl='" + getImagenUrl() + "'" +
            ", activo='" + getActivo() + "'" +
            ", destacado='" + getDestacado() + "'" +
            "}";
    }
}
