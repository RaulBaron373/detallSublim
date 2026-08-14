package com.detallsublim.app.service.dto;

import com.detallsublim.app.domain.enumeration.EstadoSolicitud;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.detallsublim.app.domain.SolicitudPresupuesto} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SolicitudPresupuestoDTO implements Serializable {

    private Long id;

    @NotNull
    private String nombreCliente;

    @NotNull
    private String email;

    private String telefono;

    private String nombreEmpresa;

    @Lob
    private String descripcion;

    @NotNull
    @Min(value = 1)
    private Integer cantidad;

    @NotNull
    private Instant fechaSolicitud;

    @NotNull
    private EstadoSolicitud estado;

    @Lob
    private String observacionesInternas;

    private Double precioPresupuesto;

    private String tiempoEstimado;

    @Lob
    private String observacionesPresupuesto;

    private Instant fechaEnvioPresupuesto;

    private ProductoDTO producto;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Instant getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(Instant fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }

    public String getObservacionesInternas() {
        return observacionesInternas;
    }

    public void setObservacionesInternas(String observacionesInternas) {
        this.observacionesInternas = observacionesInternas;
    }

    public Double getPrecioPresupuesto() {
        return precioPresupuesto;
    }

    public void setPrecioPresupuesto(Double precioPresupuesto) {
        this.precioPresupuesto = precioPresupuesto;
    }

    public String getTiempoEstimado() {
        return tiempoEstimado;
    }

    public void setTiempoEstimado(String tiempoEstimado) {
        this.tiempoEstimado = tiempoEstimado;
    }

    public String getObservacionesPresupuesto() {
        return observacionesPresupuesto;
    }

    public void setObservacionesPresupuesto(String observacionesPresupuesto) {
        this.observacionesPresupuesto = observacionesPresupuesto;
    }

    public Instant getFechaEnvioPresupuesto() {
        return fechaEnvioPresupuesto;
    }

    public void setFechaEnvioPresupuesto(Instant fechaEnvioPresupuesto) {
        this.fechaEnvioPresupuesto = fechaEnvioPresupuesto;
    }

    public ProductoDTO getProducto() {
        return producto;
    }

    public void setProducto(ProductoDTO producto) {
        this.producto = producto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SolicitudPresupuestoDTO)) {
            return false;
        }

        SolicitudPresupuestoDTO solicitudPresupuestoDTO = (SolicitudPresupuestoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, solicitudPresupuestoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SolicitudPresupuestoDTO{" +
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
            ", producto=" + getProducto() +
            "}";
    }
}
