package com.detallsublim.app.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A MensajeContacto.
 */
@Entity
@Table(name = "mensaje_contacto")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MensajeContacto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "telefono")
    private String telefono;

    @NotNull
    @Column(name = "asunto", nullable = false)
    private String asunto;

    @Lob
    @Column(name = "mensaje", nullable = false)
    private String mensaje;

    @NotNull
    @Column(name = "fecha_envio", nullable = false)
    private Instant fechaEnvio;

    @NotNull
    @Column(name = "atendido", nullable = false)
    private Boolean atendido;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MensajeContacto id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public MensajeContacto nombre(String nombre) {
        this.setNombre(nombre);
        return this;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return this.email;
    }

    public MensajeContacto email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return this.telefono;
    }

    public MensajeContacto telefono(String telefono) {
        this.setTelefono(telefono);
        return this;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getAsunto() {
        return this.asunto;
    }

    public MensajeContacto asunto(String asunto) {
        this.setAsunto(asunto);
        return this;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getMensaje() {
        return this.mensaje;
    }

    public MensajeContacto mensaje(String mensaje) {
        this.setMensaje(mensaje);
        return this;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Instant getFechaEnvio() {
        return this.fechaEnvio;
    }

    public MensajeContacto fechaEnvio(Instant fechaEnvio) {
        this.setFechaEnvio(fechaEnvio);
        return this;
    }

    public void setFechaEnvio(Instant fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public Boolean getAtendido() {
        return this.atendido;
    }

    public MensajeContacto atendido(Boolean atendido) {
        this.setAtendido(atendido);
        return this;
    }

    public void setAtendido(Boolean atendido) {
        this.atendido = atendido;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MensajeContacto)) {
            return false;
        }
        return getId() != null && getId().equals(((MensajeContacto) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MensajeContacto{" +
            "id=" + getId() +
            ", nombre='" + getNombre() + "'" +
            ", email='" + getEmail() + "'" +
            ", telefono='" + getTelefono() + "'" +
            ", asunto='" + getAsunto() + "'" +
            ", mensaje='" + getMensaje() + "'" +
            ", fechaEnvio='" + getFechaEnvio() + "'" +
            ", atendido='" + getAtendido() + "'" +
            "}";
    }
}
