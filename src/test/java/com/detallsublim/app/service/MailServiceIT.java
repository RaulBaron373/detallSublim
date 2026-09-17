package com.detallsublim.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.detallsublim.app.IntegrationTest;
import com.detallsublim.app.config.Constants;
import com.detallsublim.app.domain.User;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.Attachment;
import com.resend.services.emails.model.CreateEmailOptions;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.util.HtmlUtils;

/**
 * Integration tests for {@link MailService}.
 */
@IntegrationTest
class MailServiceIT {

    private static final String[] languages = {
        "es",
        "ca",
        "en",
        // jhipster-needle-i18n-language-constant - JHipster will add/remove languages in this array
    };

    private static final Pattern PATTERN_LOCALE_3 = Pattern.compile("([a-z]{2})-([a-zA-Z]{4})-([a-z]{2})");

    private static final Pattern PATTERN_LOCALE_2 = Pattern.compile("([a-z]{2})-([a-z]{2})");

    @MockitoBean
    private ResendEmailClient resendEmailClient;

    @Captor
    private ArgumentCaptor<CreateEmailOptions> messageCaptor;

    @Autowired
    private MailService mailService;

    @BeforeEach
    void setup() {
        reset(resendEmailClient);
        when(resendEmailClient.isConfigured()).thenReturn(true);
    }

    @Test
    void testSendEmail() throws Exception {
        CreateEmailOptions email = captureSentEmail(() ->
            mailService.sendEmail("john.doe@example.com", "testSubject", "testContent", false, false)
        );

        assertThat(email.getSubject()).isEqualTo("testSubject");
        assertThat(email.getTo()).containsExactly("john.doe@example.com");
        assertThat(email.getFrom()).contains("info@mail.detallsublim.es");
        assertThat(email.getText()).isEqualTo("testContent");
        assertThat(email.getHtml()).isNull();
        assertThat(email.getAttachments()).isNullOrEmpty();
    }

    @Test
    void testSendHtmlEmail() throws Exception {
        CreateEmailOptions email = captureSentEmail(() ->
            mailService.sendEmail("john.doe@example.com", "testSubject", "testContent", false, true)
        );

        assertThat(email.getSubject()).isEqualTo("testSubject");
        assertThat(email.getTo()).containsExactly("john.doe@example.com");
        assertThat(email.getFrom()).contains("info@mail.detallsublim.es");
        assertThat(email.getHtml()).isEqualTo("testContent");
        assertThat(email.getText()).isNull();

        assertThat(email.getAttachments()).extracting(Attachment::getContentId).contains("detallSublimLogoLight", "detallSublimLogoDark");
    }

    @Test
    void testSendMultipartEmail() throws Exception {
        CreateEmailOptions email = captureSentEmail(() ->
            mailService.sendEmail("john.doe@example.com", "testSubject", "testContent", true, false)
        );

        assertThat(email.getSubject()).isEqualTo("testSubject");
        assertThat(email.getTo()).containsExactly("john.doe@example.com");
        assertThat(email.getText()).isEqualTo("testContent");
    }

    @Test
    void testSendMultipartHtmlEmail() throws Exception {
        CreateEmailOptions email = captureSentEmail(() ->
            mailService.sendEmail("john.doe@example.com", "testSubject", "testContent", true, true)
        );

        assertThat(email.getSubject()).isEqualTo("testSubject");
        assertThat(email.getTo()).containsExactly("john.doe@example.com");
        assertThat(email.getHtml()).isEqualTo("testContent");

        assertThat(email.getAttachments()).extracting(Attachment::getContentId).contains("detallSublimLogoLight", "detallSublimLogoDark");
    }

    @Test
    void testSendEmailFromTemplate() throws Exception {
        User user = new User();
        user.setLangKey(Constants.DEFAULT_LANGUAGE);
        user.setLogin("john");
        user.setEmail("john.doe@example.com");

        CreateEmailOptions email = captureSentEmail(() -> mailService.sendEmailFromTemplate(user, "mail/testEmail", "email.test.title"));

        assertThat(email.getSubject()).isEqualTo("test title");
        assertThat(email.getTo()).containsExactly(user.getEmail());
        assertThat(email.getHtml()).contains("test title").contains("http://127.0.0.1:8080").contains("john").contains("Detall Sublim");
    }

    @Test
    void testSendActivationEmail() throws Exception {
        User user = new User();
        user.setLangKey(Constants.DEFAULT_LANGUAGE);
        user.setLogin("john");
        user.setEmail("john.doe@example.com");

        CreateEmailOptions email = captureSentEmail(() -> mailService.sendActivationEmail(user));

        assertThat(email.getTo()).containsExactly(user.getEmail());
        assertThat(email.getHtml()).isNotBlank().contains("Detall Sublim");
    }

    @Test
    void testCreationEmail() throws Exception {
        User user = new User();
        user.setLangKey(Constants.DEFAULT_LANGUAGE);
        user.setLogin("john");
        user.setEmail("john.doe@example.com");

        CreateEmailOptions email = captureSentEmail(() -> mailService.sendCreationEmail(user));

        assertThat(email.getTo()).containsExactly(user.getEmail());
        assertThat(email.getHtml()).isNotBlank().contains("Detall Sublim");
    }

    @Test
    void testSendPasswordResetMail() throws Exception {
        User user = new User();
        user.setLangKey(Constants.DEFAULT_LANGUAGE);
        user.setLogin("john");
        user.setEmail("john.doe@example.com");

        CreateEmailOptions email = captureSentEmail(() -> mailService.sendPasswordResetMail(user));

        assertThat(email.getTo()).containsExactly(user.getEmail());
        assertThat(email.getHtml()).isNotBlank().contains("Detall Sublim");
    }

    @Test
    void testSendEmailWithException() throws Exception {
        doThrow(new ResendException("Simulated Resend failure")).when(resendEmailClient).send(any(CreateEmailOptions.class));

        assertThatCode(() -> mailService.sendEmail("john.doe@example.com", "testSubject", "testContent", false, false)
        ).doesNotThrowAnyException();

        verify(resendEmailClient, timeout(1000)).send(any(CreateEmailOptions.class));
    }

    @Test
    void testSendLocalizedEmailForAllSupportedLanguages() throws Exception {
        User user = new User();
        user.setLogin("john");
        user.setEmail("john.doe@example.com");

        for (String langKey : languages) {
            user.setLangKey(langKey);

            CreateEmailOptions email = captureSentEmail(() -> mailService.sendEmailFromTemplate(user, "mail/testEmail", "email.test.title")
            );

            String propertyFilePath = "i18n/messages_" + getMessageSourceSuffixForLanguage(langKey) + ".properties";
            URL resource = this.getClass().getClassLoader().getResource(propertyFilePath);

            assertThat(resource).isNotNull();

            Path filePath = Path.of(resource.toURI());
            Properties properties = new Properties();

            properties.load(new InputStreamReader(Files.newInputStream(filePath), Charset.forName("UTF-8")));

            String emailTitle = (String) properties.get("email.test.title");

            assertThat(email.getSubject()).isEqualTo(emailTitle);
            assertThat(email.getHtml()).contains(emailTitle).contains("http://127.0.0.1:8080").contains("john").contains("Detall Sublim");
        }
    }

    @Test
    void testSendBrandedDetailsEmailWithPdfAttachment() throws Exception {
        Map<String, String> details = new LinkedHashMap<>();
        details.put("Producto", "Camiseta personalizada");
        details.put("Cantidad", "25");
        details.put("Precio estimado", "350,00 €");
        details.put("Tiempo estimado", "Por definir");
        details.put("Referencia", "#1500");

        byte[] pdf = "%PDF-1.7\nDetall Sublim test".getBytes(StandardCharsets.UTF_8);
        String filename = "Presupuesto-DS-2026-001500.pdf";

        CreateEmailOptions email = captureSentEmail(() ->
            mailService.sendBrandedDetailsEmailWithAttachment(
                "cliente@example.com",
                "Tu presupuesto - Detall Sublim",
                "PRESUPUESTO",
                "Tu presupuesto está preparado",
                "Hemos preparado el presupuesto correspondiente a tu solicitud.",
                details,
                "Producción según diseño y cantidades confirmadas.",
                filename,
                pdf
            )
        );

        assertThat(email.getSubject()).isEqualTo("Tu presupuesto - Detall Sublim");
        assertThat(email.getTo()).containsExactly("cliente@example.com");

        String decodedHtml = HtmlUtils.htmlUnescape(email.getHtml());

        assertThat(decodedHtml)
            .contains("PRESUPUESTO")
            .contains("Tu presupuesto está preparado")
            .contains("Camiseta personalizada")
            .contains("350,00 €")
            .contains("Tiempo estimado")
            .contains("Producción según diseño y cantidades confirmadas.");

        Attachment pdfAttachment = email
            .getAttachments()
            .stream()
            .filter(attachment -> filename.equals(attachment.getFileName()))
            .findFirst()
            .orElse(null);

        assertThat(pdfAttachment).isNotNull();
        assertThat(pdfAttachment.getContentType()).isEqualTo("application/pdf");

        byte[] decodedPdf = Base64.getDecoder().decode(pdfAttachment.getContent());

        assertThat(decodedPdf).isEqualTo(pdf);
    }

    private CreateEmailOptions captureSentEmail(Runnable sendAction) throws ResendException {
        clearInvocations(resendEmailClient);

        sendAction.run();

        verify(resendEmailClient, timeout(1000)).send(messageCaptor.capture());

        return messageCaptor.getValue();
    }

    /**
     * Convert a lang key to the Java locale.
     */
    private String getMessageSourceSuffixForLanguage(String langKey) {
        String javaLangKey = langKey;

        Matcher matcher2 = PATTERN_LOCALE_2.matcher(langKey);

        if (matcher2.matches()) {
            javaLangKey = matcher2.group(1) + "_" + matcher2.group(2).toUpperCase();
        }

        Matcher matcher3 = PATTERN_LOCALE_3.matcher(langKey);

        if (matcher3.matches()) {
            javaLangKey = matcher3.group(1) + "_" + matcher3.group(2) + "_" + matcher3.group(3).toUpperCase();
        }

        return javaLangKey;
    }
}
