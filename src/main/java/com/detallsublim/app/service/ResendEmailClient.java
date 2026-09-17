package com.detallsublim.app.service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ResendEmailClient {

    private final Resend resend;

    public ResendEmailClient(@Value("${resend.api-key:}") String apiKey) {
        this.resend = apiKey == null || apiKey.isBlank() ? null : new Resend(apiKey);
    }

    public boolean isConfigured() {
        return resend != null;
    }

    public void send(CreateEmailOptions email) throws ResendException {
        if (resend == null) {
            throw new IllegalStateException("RESEND_API_KEY is not configured");
        }

        resend.emails().send(email);
    }
}
