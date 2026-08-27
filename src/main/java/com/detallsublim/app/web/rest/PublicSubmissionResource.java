package com.detallsublim.app.web.rest;

import com.detallsublim.app.domain.enumeration.EstadoSolicitud;
import com.detallsublim.app.service.MensajeContactoService;
import com.detallsublim.app.service.ProductoService;
import com.detallsublim.app.service.RateLimitService;
import com.detallsublim.app.service.SolicitudPresupuestoService;
import com.detallsublim.app.service.dto.MensajeContactoDTO;
import com.detallsublim.app.service.dto.ProductoDTO;
import com.detallsublim.app.service.dto.SolicitudPresupuestoDTO;
import com.detallsublim.app.web.rest.errors.BadRequestAlertException;
import com.detallsublim.app.web.rest.vm.PublicMensajeContactoVM;
import com.detallsublim.app.web.rest.vm.PublicSolicitudPresupuestoVM;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.Duration;
import java.time.Instant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
public class PublicSubmissionResource {

    private static final String SOLICITUD_ENTITY_NAME = "solicitudPresupuesto";

    private final MensajeContactoService mensajeContactoService;

    private final SolicitudPresupuestoService solicitudPresupuestoService;

    private final ProductoService productoService;

    private final RateLimitService rateLimitService;

    public PublicSubmissionResource(
        MensajeContactoService mensajeContactoService,
        SolicitudPresupuestoService solicitudPresupuestoService,
        ProductoService productoService,
        RateLimitService rateLimitService
    ) {
        this.mensajeContactoService = mensajeContactoService;

        this.solicitudPresupuestoService = solicitudPresupuestoService;

        this.productoService = productoService;

        this.rateLimitService = rateLimitService;
    }

    @PostMapping("/contact")
    public ResponseEntity<Void> createContactMessage(@Valid @RequestBody PublicMensajeContactoVM requestBody, HttpServletRequest request) {
        String rateLimitKey = rateLimitService.clientKey("public-contact", request);

        RateLimitService.Result limit = rateLimitService.consume(rateLimitKey, 5, Duration.ofMinutes(10));

        if (!limit.allowed()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header(HttpHeaders.RETRY_AFTER, String.valueOf(limit.retryAfterSeconds()))
                .build();
        }
        MensajeContactoDTO mensaje = new MensajeContactoDTO();

        mensaje.setNombre(requestBody.getNombre().trim());

        mensaje.setEmail(requestBody.getEmail().trim());

        mensaje.setTelefono(normalizeOptional(requestBody.getTelefono()));

        mensaje.setAsunto(requestBody.getAsunto().trim());

        mensaje.setMensaje(requestBody.getMensaje().trim());

        /*
         * Estos valores nunca proceden
         * del navegador.
         */
        mensaje.setFechaEnvio(Instant.now());

        mensaje.setAtendido(false);

        mensajeContactoService.save(mensaje);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/quote-request")
    public ResponseEntity<Void> createQuoteRequest(
        @Valid @RequestBody PublicSolicitudPresupuestoVM requestBody,
        HttpServletRequest request
    ) {
        String rateLimitKey = rateLimitService.clientKey("public-contact", request);

        RateLimitService.Result limit = rateLimitService.consume(rateLimitKey, 5, Duration.ofMinutes(10));

        if (!limit.allowed()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header(HttpHeaders.RETRY_AFTER, String.valueOf(limit.retryAfterSeconds()))
                .build();
        }

        ProductoDTO producto = null;

        if (requestBody.getProductoId() != null) {
            producto = productoService
                .findOne(requestBody.getProductoId())
                .filter(item -> Boolean.TRUE.equals(item.getActivo()))
                .orElseThrow(() -> new BadRequestAlertException("Producto no disponible", SOLICITUD_ENTITY_NAME, "productnotavailable"));
        }

        SolicitudPresupuestoDTO solicitud = new SolicitudPresupuestoDTO();

        solicitud.setNombreCliente(requestBody.getNombreCliente().trim());

        solicitud.setEmail(requestBody.getEmail().trim());

        solicitud.setTelefono(requestBody.getTelefono().trim());

        solicitud.setNombreEmpresa(normalizeOptional(requestBody.getNombreEmpresa()));

        solicitud.setDescripcion(requestBody.getDescripcion().trim());

        solicitud.setCantidad(requestBody.getCantidad());

        solicitud.setProducto(producto);

        /*
         * Todos los campos administrativos
         * son establecidos exclusivamente
         * por el servidor.
         */
        solicitud.setFechaSolicitud(Instant.now());

        solicitud.setEstado(EstadoSolicitud.PENDIENTE);

        solicitud.setObservacionesInternas(null);

        solicitud.setPrecioPresupuesto(null);

        solicitud.setTiempoEstimado(null);

        solicitud.setObservacionesPresupuesto(null);

        solicitud.setFechaEnvioPresupuesto(null);

        solicitudPresupuestoService.save(solicitud);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    private static String normalizeOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}
