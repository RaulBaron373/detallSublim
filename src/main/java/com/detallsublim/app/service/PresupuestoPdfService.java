package com.detallsublim.app.service;

import com.detallsublim.app.domain.SolicitudPresupuesto;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Locale;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class PresupuestoPdfService {

    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Europe/Madrid");
    private static final Locale BUSINESS_LOCALE = Locale.forLanguageTag("es-ES");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(BUSINESS_ZONE);

    private static final String COMPANY_NAME = "Detall Sublim";
    private static final String COMPANY_ADDRESS_LINE_1 = "C/ Pareto, 0 · Mercat Santa Eulàlia";
    private static final String COMPANY_ADDRESS_LINE_2 = "Paradas 39-40 · 08902 L'Hospitalet";
    private static final String COMPANY_EMAIL = "pedidosdetallsublim@gmail.com";
    private static final String COMPANY_SECONDARY_EMAIL = "detallsublim.es@gmail.com";
    private static final String COMPANY_PHONE = "+34 618 006 543";
    private static final String COMPANY_WEB = "detallsublim.es";

    private final TemplateEngine templateEngine;

    public PresupuestoPdfService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public byte[] generar(SolicitudPresupuesto solicitud) {
        validarSolicitud(solicitud);

        Context context = new Context(BUSINESS_LOCALE);

        String numeroPresupuesto = generarNumeroPresupuesto(solicitud);

        context.setVariable("companyName", COMPANY_NAME);
        context.setVariable("companyAddressLine1", COMPANY_ADDRESS_LINE_1);
        context.setVariable("companyAddressLine2", COMPANY_ADDRESS_LINE_2);
        context.setVariable("companyEmail", COMPANY_EMAIL);
        context.setVariable("companySecondaryEmail", COMPANY_SECONDARY_EMAIL);
        context.setVariable("companyPhone", COMPANY_PHONE);
        context.setVariable("companyWeb", COMPANY_WEB);
        context.setVariable("logoDataUri", cargarLogo());

        context.setVariable("numeroPresupuesto", numeroPresupuesto);
        context.setVariable("fechaEmision", DATE_FORMATTER.format(solicitud.getFechaEnvioPresupuesto()));

        context.setVariable("nombreCliente", solicitud.getNombreCliente());
        context.setVariable("empresaCliente", valorOpcional(solicitud.getNombreEmpresa()));
        context.setVariable("emailCliente", solicitud.getEmail());
        context.setVariable("telefonoCliente", valorOpcional(solicitud.getTelefono()));

        context.setVariable(
            "producto",
            solicitud.getProducto() != null && StringUtils.hasText(solicitud.getProducto().getNombre())
                ? solicitud.getProducto().getNombre()
                : "Producto personalizado"
        );

        context.setVariable("descripcion", solicitud.getDescripcion());
        context.setVariable("cantidad", solicitud.getCantidad());

        String importe = formatearImporte(solicitud.getPrecioPresupuesto());

        context.setVariable("importe", importe);
        context.setVariable("importeTotal", importe);
        context.setVariable(
            "observaciones",
            StringUtils.hasText(solicitud.getObservacionesPresupuesto())
                ? solicitud.getObservacionesPresupuesto()
                : "Sin observaciones adicionales."
        );

        String html = templateEngine.process("pdf/presupuesto", context);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();

            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(outputStream);
            builder.run();

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo generar el PDF del presupuesto " + numeroPresupuesto, e);
        }
    }

    public String generarNombreArchivo(SolicitudPresupuesto solicitud) {
        return "Presupuesto-" + generarNumeroPresupuesto(solicitud) + ".pdf";
    }

    String generarNumeroPresupuesto(SolicitudPresupuesto solicitud) {
        validarSolicitud(solicitud);

        int year = solicitud.getFechaEnvioPresupuesto().atZone(BUSINESS_ZONE).getYear();

        return String.format(Locale.ROOT, "DS-%d-%06d", year, solicitud.getId());
    }

    private String formatearImporte(BigDecimal importe) {
        if (importe == null) {
            return "Por definir";
        }

        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(BUSINESS_LOCALE);
        DecimalFormat formatter = new DecimalFormat("#,##0.00 '€'", symbols);

        return formatter.format(importe);
    }

    private String valorOpcional(String value) {
        return StringUtils.hasText(value) ? value : "—";
    }

    private String cargarLogo() {
        ClassPathResource logo = new ClassPathResource("templates/pdf/logo-detall-sublim-pdf.png");

        try (var inputStream = logo.getInputStream()) {
            String base64 = Base64.getEncoder().encodeToString(inputStream.readAllBytes());
            return "data:image/png;base64," + base64;
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo cargar el logo de Detall Sublim para el presupuesto", e);
        }
    }

    private void validarSolicitud(SolicitudPresupuesto solicitud) {
        if (solicitud == null) {
            throw new IllegalArgumentException("La solicitud no puede ser nula");
        }

        if (solicitud.getId() == null) {
            throw new IllegalArgumentException("La solicitud debe estar persistida antes de generar el presupuesto");
        }

        if (solicitud.getFechaEnvioPresupuesto() == null) {
            throw new IllegalArgumentException("El presupuesto debe tener fecha de emisión");
        }
    }
}
