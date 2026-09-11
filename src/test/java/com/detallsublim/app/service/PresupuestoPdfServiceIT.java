package com.detallsublim.app.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.detallsublim.app.IntegrationTest;
import com.detallsublim.app.domain.Producto;
import com.detallsublim.app.domain.SolicitudPresupuesto;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class PresupuestoPdfServiceIT {

    @Autowired
    private PresupuestoPdfService presupuestoPdfService;

    @Test
    void shouldGenerateProfessionalBudgetPdf() throws Exception {
        Producto producto = new Producto();
        producto.setNombre("Camiseta personalizada");

        SolicitudPresupuesto solicitud = new SolicitudPresupuesto()
            .id(1500L)
            .nombreCliente("Cliente de prueba")
            .nombreEmpresa("Empresa de prueba")
            .email("cliente@example.com")
            .telefono("+34 600 000 000")
            .descripcion("Personalización de camisetas con diseño facilitado por el cliente.")
            .cantidad(25)
            .precioPresupuesto(new BigDecimal("350.00"))
            .observacionesPresupuesto("Producción según diseño y cantidades confirmadas.")
            .fechaEnvioPresupuesto(Instant.parse("2026-09-11T18:30:00Z"))
            .producto(producto);

        byte[] pdf = presupuestoPdfService.generar(solicitud);

        assertThat(pdf).isNotEmpty();
        assertThat(pdf.length).isGreaterThan(5_000);
        assertThat(new String(pdf, 0, 5)).isEqualTo("%PDF-");

        try (var document = Loader.loadPDF(pdf)) {
            assertThat(document.getNumberOfPages()).isEqualTo(1);

            String text = new PDFTextStripper().getText(document);

            assertThat(text)
                .contains("PRESUPUESTO")
                .contains("DS-2026-001500")
                .contains("Cliente de prueba")
                .contains("Camiseta personalizada")
                .contains("350,00 €")
                .doesNotContain("PLAZO ESTIMADO")
                .doesNotContain("CONDICIONES GENERALES");
        }

        Files.write(Path.of("target", "presupuesto-preview.pdf"), pdf);
    }
}
