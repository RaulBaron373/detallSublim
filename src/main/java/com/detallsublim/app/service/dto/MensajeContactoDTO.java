package com.detallsublim.app.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.detallsublim.app.domain.MensajeContacto} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MensajeContactoDTO implements Serializable {

    private Long id;

    @NotBlank
    @Size(max = 100)
    private String nombre;

    @NotBlank
    @Email
    @Size(max = 254)
    private String email;

    @Size(max = 30)
    private String telefono;

    @NotBlank
    @Size(max = 150)
    private String asunto;

    @Lob
    @NotBlank
    @Size(max = 5000)
    private String mensaje;

    @NotNull
    private Instant fechaEnvio;

    @NotNull
    private Boolean atendido;

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

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Instant getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(Instant fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public Boolean getAtendido() {
        return atendido;
    }

    public void setAtendido(Boolean atendido) {
        this.atendido = atendido;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MensajeContactoDTO)) {
            return false;
        }

        MensajeContactoDTO mensajeContactoDTO = (MensajeContactoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, mensajeContactoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MensajeContactoDTO{" +
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
