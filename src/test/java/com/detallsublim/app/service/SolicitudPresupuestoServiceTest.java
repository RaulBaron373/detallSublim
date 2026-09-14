package com.detallsublim.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.detallsublim.app.domain.SolicitudPresupuesto;
import com.detallsublim.app.domain.enumeration.EstadoSolicitud;
import com.detallsublim.app.repository.SolicitudPresupuestoRepository;
import com.detallsublim.app.service.dto.SolicitudPresupuestoDTO;
import com.detallsublim.app.service.mapper.SolicitudPresupuestoMapper;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SolicitudPresupuestoServiceTest {

    @Mock
    private SolicitudPresupuestoRepository solicitudPresupuestoRepository;

    @Mock
    private SolicitudPresupuestoMapper solicitudPresupuestoMapper;

    @Mock
    private MailService mailService;

    @Mock
    private PresupuestoPdfService presupuestoPdfService;

    @InjectMocks
    private SolicitudPresupuestoService solicitudPresupuestoService;

    @Test
    void rejectedEmailShouldNotExposeInternalObservations() {
        Long id = 1L;
        String internalNote = "NO COMPARTIR: margen insuficiente";

        SolicitudPresupuesto existing = new SolicitudPresupuesto();
        existing.setId(id);
        existing.setEstado(EstadoSolicitud.PENDIENTE);

        SolicitudPresupuestoDTO dto = new SolicitudPresupuestoDTO();
        dto.setId(id);
        dto.setEstado(EstadoSolicitud.RECHAZADO);
        dto.setEmail("cliente@example.com");
        dto.setNombreCliente("Cliente");
        dto.setObservacionesInternas(internalNote);

        SolicitudPresupuesto updated = new SolicitudPresupuesto();
        updated.setId(id);
        updated.setEstado(EstadoSolicitud.RECHAZADO);
        updated.setEmail("cliente@example.com");
        updated.setNombreCliente("Cliente");
        updated.setObservacionesInternas(internalNote);

        when(solicitudPresupuestoRepository.findById(id)).thenReturn(Optional.of(existing));
        when(solicitudPresupuestoMapper.toEntity(dto)).thenReturn(updated);
        when(solicitudPresupuestoRepository.save(updated)).thenReturn(updated);
        when(solicitudPresupuestoMapper.toDto(updated)).thenReturn(dto);

        solicitudPresupuestoService.update(dto);

        ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);

        verify(mailService).sendBrandedTextEmail(
            eq("cliente@example.com"),
            eq("Actualización de tu solicitud - Detall Sublim"),
            eq("SOLICITUD ACTUALIZADA"),
            eq("Información sobre tu solicitud"),
            contentCaptor.capture()
        );

        assertThat(contentCaptor.getValue())
            .doesNotContain(internalNote)
            .doesNotContain("Motivo:")
            .contains("En esta ocasión no podremos continuar con tu solicitud.")
            .contains("Si necesitas alguna aclaración, puedes ponerte en contacto con nosotros.");
    }
}
