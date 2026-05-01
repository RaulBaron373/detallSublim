package com.detallsublim.app.domain;

import com.detallsublim.app.domain.enumeration.EstadoSolicitud;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A SolicitudPresupuesto.
 */
@Entity
@Table(name = "solicitud_presupuesto")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SolicitudPresupuesto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "nombre_cliente", nullable = false)
    private String nombreCliente;

    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "nombre_empresa")
    private String nombreEmpresa;

    @Lob
    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @NotNull
    @Min(value = 1)
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @NotNull
    @Column(name = "fecha_solicitud", nullable = false)
    private Instant fechaSolicitud;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoSolicitud estado;

    @Lob
    @Column(name = "observaciones_internas")
    private String observacionesInternas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "categoria" }, allowSetters = true)
    private Producto producto;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SolicitudPresupuesto id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreCliente() {
        return this.nombreCliente;
    }

    public SolicitudPresupuesto nombreCliente(String nombreCliente) {
        this.setNombreCliente(nombreCliente);
        return this;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getEmail() {
        return this.email;
    }

    public SolicitudPresupuesto email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return this.telefono;
    }

    public SolicitudPresupuesto telefono(String telefono) {
        this.setTelefono(telefono);
        return this;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNombreEmpresa() {
        return this.nombreEmpresa;
    }

    public SolicitudPresupuesto nombreEmpresa(String nombreEmpresa) {
        this.setNombreEmpresa(nombreEmpresa);
        return this;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public SolicitudPresupuesto descripcion(String descripcion) {
        this.setDescripcion(descripcion);
        return this;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getCantidad() {
        return this.cantidad;
    }

    public SolicitudPresupuesto cantidad(Integer cantidad) {
        this.setCantidad(cantidad);
        return this;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Instant getFechaSolicitud() {
        return this.fechaSolicitud;
    }

    public SolicitudPresupuesto fechaSolicitud(Instant fechaSolicitud) {
        this.setFechaSolicitud(fechaSolicitud);
        return this;
    }

    public void setFechaSolicitud(Instant fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public EstadoSolicitud getEstado() {
        return this.estado;
    }

    public SolicitudPresupuesto estado(EstadoSolicitud estado) {
        this.setEstado(estado);
        return this;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }

    public String getObservacionesInternas() {
        return this.observacionesInternas;
    }

    public SolicitudPresupuesto observacionesInternas(String observacionesInternas) {
        this.setObservacionesInternas(observacionesInternas);
        return this;
    }

    public void setObservacionesInternas(String observacionesInternas) {
        this.observacionesInternas = observacionesInternas;
    }

    public Producto getProducto() {
        return this.producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public SolicitudPresupuesto producto(Producto producto) {
        this.setProducto(producto);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SolicitudPresupuesto)) {
            return false;
        }
        return getId() != null && getId().equals(((SolicitudPresupuesto) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SolicitudPresupuesto{" +
            "id=" + getId() +
            ", nombreCliente='" + getNombreCliente() + "'" +
            ", email='" + getEmail() + "'" +
            ", telefono='" + getTelefono() + "'" +
            ", nombreEmpresa='" + getNombreEmpresa() + "'" +
            ", descripcion='" + getDescripcion() + "'" +
            ", cantidad=" + getCantidad() +
            ", fechaSolicitud='" + getFechaSolicitud() + "'" +
            ", estado='" + getEstado() + "'" +
            ", observacionesInternas='" + getObservacionesInternas() + "'" +
            "}";
    }
}
