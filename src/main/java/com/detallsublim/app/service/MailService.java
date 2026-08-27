package com.detallsublim.app.service;

import com.detallsublim.app.domain.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import tech.jhipster.config.JHipsterProperties;

/**
 * Service for sending emails asynchronously.
 * <p>
 * We use the {@link Async} annotation to send emails asynchronously.
 */
@Service
public class MailService {

    private static final Logger LOG = LoggerFactory.getLogger(MailService.class);

    private static final String USER = "user";

    private static final String BASE_URL = "baseUrl";

    private final JHipsterProperties jHipsterProperties;

    private final JavaMailSender javaMailSender;

    private final MessageSource messageSource;

    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username:}")
    private String companyEmail;

    public MailService(
        JHipsterProperties jHipsterProperties,
        JavaMailSender javaMailSender,
        MessageSource messageSource,
        SpringTemplateEngine templateEngine
    ) {
        this.jHipsterProperties = jHipsterProperties;
        this.javaMailSender = javaMailSender;
        this.messageSource = messageSource;
        this.templateEngine = templateEngine;
    }

    @Async
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        sendEmailSync(to, subject, content, isMultipart, isHtml);
    }

    private void sendEmailSync(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        LOG.debug(
            "Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
            isMultipart,
            isHtml,
            to,
            subject,
            content
        );

        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            boolean multipart = isMultipart || isHtml;

            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, multipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setFrom(jHipsterProperties.getMail().getFrom());
            message.setSubject(subject);
            message.setText(content, isHtml);
            if (isHtml) {
                ClassPathResource lightLogo = new ClassPathResource("templates/mail/logo-detall-sublim-color.png");

                ClassPathResource darkLogo = new ClassPathResource("templates/mail/logo-detall-sublim-color-white.png");

                if (lightLogo.exists()) {
                    message.addInline("detallSublimLogoLight", lightLogo, "image/png");
                }

                if (darkLogo.exists()) {
                    message.addInline("detallSublimLogoDark", darkLogo, "image/png");
                }
            }
            javaMailSender.send(mimeMessage);
            LOG.debug("Sent email to User '{}'", to);
        } catch (MailException | MessagingException e) {
            LOG.warn("Email could not be sent to user '{}'", to, e);
        }
    }

    @Async
    public void sendEmailFromTemplate(User user, String templateName, String titleKey) {
        sendEmailFromTemplateSync(user, templateName, titleKey);
    }

    private void sendEmailFromTemplateSync(User user, String templateName, String titleKey) {
        if (user.getEmail() == null) {
            LOG.debug("Email doesn't exist for user '{}'", user.getLogin());
            return;
        }
        Locale locale = Locale.forLanguageTag(user.getLangKey());
        Context context = new Context(locale);
        context.setVariable(USER, user);
        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
        String templateContent = templateEngine.process(templateName, context);

        String content = wrapInDetallSublimTemplate(templateContent);
        String subject = messageSource.getMessage(titleKey, null, locale);
        sendEmailSync(user.getEmail(), subject, content, true, true);
    }

    private String wrapInDetallSublimTemplate(String bodyContent) {
        return """
        <!doctype html>
        <html lang="es">
        <head>
            <meta charset="UTF-8">

            <meta
                name="viewport"
                content="width=device-width, initial-scale=1.0"
            >

            <meta
                name="color-scheme"
                content="light dark"
            >

            <meta
                name="supported-color-schemes"
                content="light dark"
            >

            <style>
                .ds-logo-dark {
                    display: none !important;
                    max-height: 0 !important;
                    overflow: hidden !important;
                    mso-hide: all !important;
                }

                @media (prefers-color-scheme: dark) {
                    body,
                    .ds-email-background {
                        background: #111318 !important;
                    }

                    .ds-email-card {
                        background: #1b1d23 !important;
                        border-color: #30333b !important;
                    }

                    .ds-logo-light {
                        display: none !important;
                        max-height: 0 !important;
                        overflow: hidden !important;
                    }

                    .ds-logo-dark {
                        display: block !important;
                        max-height: none !important;
                        overflow: visible !important;
                        mso-hide: none !important;
                    }

                    .ds-email-card [style*="color:#081a35"],
                    .ds-email-card [style*="color:#202534"] {
                        color: #f5f6fa !important;
                    }

                    .ds-email-card [style*="color:#555b68"] {
                        color: #c8ccd5 !important;
                    }

                    .ds-email-card [style*="color:#8a8f9c"] {
                        color: #aeb3bd !important;
                    }

                    .ds-email-card [style*="background:#f7f8fa"] {
                        background: #23262d !important;
                    }

                    .ds-email-card [style*="background:#fafafb"] {
                        background: #16181d !important;
                    }

                    .ds-email-card [style*="background:#fff8fb"] {
                        background: #2a2026 !important;
                    }

                    .ds-email-card [style*="border:1px solid #eceef2"] {
                        border-color: #343844 !important;
                    }

                    .ds-email-card [style*="border:1px solid #f6d9e7"] {
                        border-color: #533041 !important;
                    }

                    .ds-email-card [style*="border-top:1px solid #eeeeef"] {
                        border-color: #30333b !important;
                    }
                }

                [data-ogsc] .ds-email-background {
                    background: #111318 !important;
                }

                [data-ogsc] .ds-email-card {
                    background: #1b1d23 !important;
                    border-color: #30333b !important;
                }

                [data-ogsc] .ds-logo-light {
                    display: none !important;
                    max-height: 0 !important;
                    overflow: hidden !important;
                }

                [data-ogsc] .ds-logo-dark {
                    display: block !important;
                    max-height: none !important;
                    overflow: visible !important;
                }

                [data-ogsc] .ds-email-card [style*="color:#081a35"],
                [data-ogsc] .ds-email-card [style*="color:#202534"] {
                    color: #f5f6fa !important;
                }

                [data-ogsc] .ds-email-card [style*="color:#555b68"] {
                    color: #c8ccd5 !important;
                }

                [data-ogsc] .ds-email-card [style*="color:#8a8f9c"] {
                    color: #aeb3bd !important;
                }

                [data-ogsc] .ds-email-card [style*="background:#f7f8fa"] {
                    background: #23262d !important;
                }

                [data-ogsc] .ds-email-card [style*="background:#fafafb"] {
                    background: #16181d !important;
                }

                [data-ogsc] .ds-email-card [style*="background:#fff8fb"] {
                    background: #2a2026 !important;
                }

                [data-ogsc] .ds-email-card [style*="border:1px solid #eceef2"] {
                    border-color: #343844 !important;
                }

                [data-ogsc] .ds-email-card [style*="border:1px solid #f6d9e7"] {
                    border-color: #533041 !important;
                }

                [data-ogsc] .ds-email-card [style*="border-top:1px solid #eeeeef"] {
                    border-color: #30333b !important;
                }
            </style>
        </head>

        <body
            class="ds-email-background"
            style="
            margin:0;
            padding:0;
            background:#f5f6fa;
            font-family:Arial, Helvetica, sans-serif;
            color:#202534;
        ">

            <table
                class="ds-email-background"
                role="presentation"
                width="100%%"
                cellspacing="0"
                cellpadding="0"
                border="0"
                style="background:#f5f6fa;"
            >
                <tr>
                    <td align="center" style="padding:40px 16px;">

                        <table
                            class="ds-email-card"
                            role="presentation"
                            width="100%%"
                            cellspacing="0"
                            cellpadding="0"
                            border="0"
                            style="
                                max-width:620px;
                                background:#ffffff;
                                border-radius:24px;
                                overflow:hidden;
                                box-shadow:0 12px 40px rgba(8,26,53,.08);
                                border:1px solid #eceef3;
                            "
                        >

                            <!-- LOGO -->
                            <tr>
                                <td
                                    align="center"
                                    style="padding:32px 32px 24px;"
                                >
                                    <!-- LOGO MODO CLARO -->
                                <img
                                    class="ds-logo-light"
                                    src="cid:detallSublimLogoLight"
                                    alt="Detall Sublim"
                                    width="190"
                                    style="
                                        display:block;
                                        max-width:190px;
                                        height:auto;
                                        border:0;
                                        margin:0 auto;
                                    "
                                >

                                <!-- LOGO MODO OSCURO -->
                                <img
                                    class="ds-logo-dark"
                                    src="cid:detallSublimLogoDark"
                                    alt="Detall Sublim"
                                    width="190"
                                    style="
                                        display:none;
                                        max-height:0;
                                        overflow:hidden;
                                        max-width:190px;
                                        height:auto;
                                        border:0;
                                        margin:0 auto;
                                    "
                                >
                                </td>
                            </tr>

                            <!-- COLORES DE MARCA -->
                            <tr>
                                <td style="padding:0;">
                                    <table
                                        role="presentation"
                                        width="100%%"
                                        cellspacing="0"
                                        cellpadding="0"
                                        border="0"
                                    >
                                        <tr>
                                            <td
                                                width="40%%"
                                                height="4"
                                                style="background:#d84b8a;"
                                            ></td>

                                            <td
                                                width="20%%"
                                                height="4"
                                                style="background:#f8d545;"
                                            ></td>

                                            <td
                                                width="40%%"
                                                height="4"
                                                style="background:#00d9c0;"
                                            ></td>
                                        </tr>
                                    </table>
                                </td>
                            </tr>

                            <!-- CONTENIDO -->
                            <tr>
                                <td style="padding:36px 42px 40px;">
                                    %s
                                </td>
                            </tr>

                            <!-- FOOTER -->
                            <tr>
                                <td
                                    align="center"
                                    style="
                                        padding:24px 32px 30px;
                                        background:#fafafb;
                                        border-top:1px solid #eeeeef;
                                    "
                                >
                                    <p style="
                                        margin:0 0 6px;
                                        font-size:14px;
                                        font-weight:700;
                                        color:#202534;
                                    ">
                                        Detall Sublim
                                    </p>

                                    <p style="
                                        margin:0;
                                        font-size:12px;
                                        line-height:18px;
                                        color:#8a8f9c;
                                    ">
                                        Personalización hecha con detalle.
                                        <br>
                                        Este correo ha sido enviado automáticamente.
                                    </p>
                                </td>
                            </tr>

                        </table>

                    </td>
                </tr>
            </table>

        </body>
        </html>
        """.formatted(bodyContent);
    }

    @Async
    public void sendActivationEmail(User user) {
        LOG.debug("Sending activation email to '{}'", user.getEmail());
        sendEmailFromTemplateSync(user, "mail/activationEmail", "email.activation.title");
    }

    @Async
    public void sendCreationEmail(User user) {
        LOG.debug("Sending creation email to '{}'", user.getEmail());
        sendEmailFromTemplateSync(user, "mail/creationEmail", "email.activation.title");
    }

    @Async
    public void sendPasswordResetMail(User user) {
        LOG.debug("Sending password reset email to '{}'", user.getEmail());
        sendEmailFromTemplateSync(user, "mail/passwordResetEmail", "email.reset.title");
    }

    @Async
    public void sendCompanyNotification(String subject, String content) {
        if (companyEmail == null || companyEmail.isBlank()) {
            LOG.warn("Company notification email is not configured");
            return;
        }

        String title = subject.replace(" - Detall Sublim", "");

        String safeContent = content == null ? "" : HtmlUtils.htmlEscape(content).replace("\r\n", "\n").replace("\n", "<br>");

        String body =
            """
            <div style="
                display:inline-block;
                margin-bottom:16px;
                padding:7px 12px;
                border-radius:999px;
                background:rgba(0,217,192,.12);
                color:#008d7d;
                font-size:11px;
                font-weight:700;
                letter-spacing:.08em;
            ">
                AVISO INTERNO
            </div>

            <h1 style="
                margin:0 0 18px;
                font-size:28px;
                line-height:36px;
                color:#081a35;
            ">
                %s
            </h1>

            <div style="
                margin-top:24px;
                padding:22px;
                border-radius:16px;
                background:#f7f8fa;
                border:1px solid #eceef2;

                font-size:14px;
                line-height:23px;
                color:#555b68;
            ">
                %s
            </div>
            """.formatted(HtmlUtils.htmlEscape(title), safeContent);

        sendEmailSync(companyEmail, subject, wrapInDetallSublimTemplate(body), true, true);
    }

    @Async
    public void sendBrandedTextEmail(String to, String subject, String badge, String title, String text) {
        String safeText = text == null ? "" : HtmlUtils.htmlEscape(text).replace("\r\n", "\n").replace("\n", "<br>");

        String body =
            """
            <div style="
                display:inline-block;
                margin-bottom:16px;
                padding:7px 12px;
                border-radius:999px;
                background:rgba(216,75,138,.10);
                color:#d84b8a;
                font-size:11px;
                font-weight:700;
                letter-spacing:.08em;
            ">
                %s
            </div>

            <h1 style="
                margin:0 0 18px;
                font-size:28px;
                line-height:36px;
                color:#081a35;
            ">
                %s
            </h1>

            <div style="
                font-size:15px;
                line-height:24px;
                color:#555b68;
            ">
                %s
            </div>
            """.formatted(HtmlUtils.htmlEscape(badge), HtmlUtils.htmlEscape(title), safeText);

        sendEmailSync(to, subject, wrapInDetallSublimTemplate(body), true, true);
    }

    @Async
    public void sendBrandedDetailsEmail(
        String to,
        String subject,
        String badge,
        String title,
        String intro,
        Map<String, String> details,
        String note
    ) {
        StringBuilder rows = new StringBuilder();

        details.forEach((label, value) -> {
            String safeLabel = HtmlUtils.htmlEscape(label);
            String safeValue = HtmlUtils.htmlEscape(value != null && !value.isBlank() ? value : "—");

            rows.append(
                """
                <tr>
                    <td style="
                        padding:12px 0;
                        border-bottom:1px solid #eceef2;
                        color:#8a8f9c;
                        font-size:13px;
                        vertical-align:top;
                        width:42%%;
                    ">
                        %s
                    </td>

                    <td style="
                        padding:12px 0;
                        border-bottom:1px solid #eceef2;
                        color:#202534;
                        font-size:14px;
                        font-weight:700;
                        vertical-align:top;
                    ">
                        %s
                    </td>
                </tr>
                """.formatted(safeLabel, safeValue)
            );
        });

        String noteHtml = "";

        if (note != null && !note.isBlank()) {
            noteHtml = """
            <div style="
                margin-top:24px;
                padding:18px 20px;
                border-radius:14px;
                background:#fff8fb;
                border:1px solid #f6d9e7;
            ">
                <p style="
                    margin:0 0 7px;
                    color:#d84b8a;
                    font-size:12px;
                    font-weight:700;
                    text-transform:uppercase;
                    letter-spacing:.06em;
                ">
                    Observaciones
                </p>

                <p style="
                    margin:0;
                    color:#555b68;
                    font-size:14px;
                    line-height:22px;
                ">
                    %s
                </p>
            </div>
            """.formatted(HtmlUtils.htmlEscape(note).replace("\r\n", "\n").replace("\n", "<br>"));
        }

        String body =
            """
            <div style="
                display:inline-block;
                margin-bottom:16px;
                padding:7px 12px;
                border-radius:999px;
                background:#fcebf3;
                color:#d84b8a;
                font-size:11px;
                font-weight:700;
                letter-spacing:.08em;
            ">
                %s
            </div>

            <h1 style="
                margin:0 0 16px;
                color:#081a35;
                font-size:28px;
                line-height:36px;
            ">
                %s
            </h1>

            <p style="
                margin:0 0 26px;
                color:#555b68;
                font-size:15px;
                line-height:24px;
            ">
                %s
            </p>

            <div style="
                padding:8px 22px;
                border-radius:16px;
                background:#f7f8fa;
                border:1px solid #eceef2;
            ">
                <table
                    role="presentation"
                    width="100%%"
                    cellspacing="0"
                    cellpadding="0"
                    border="0"
                >
                    %s
                </table>
            </div>

            %s
            """.formatted(HtmlUtils.htmlEscape(badge), HtmlUtils.htmlEscape(title), HtmlUtils.htmlEscape(intro), rows, noteHtml);

        sendEmailSync(to, subject, wrapInDetallSublimTemplate(body), true, true);
    }
}
