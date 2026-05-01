package com.detallsublim.app.domain;

import static com.detallsublim.app.domain.MensajeContactoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.detallsublim.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MensajeContactoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MensajeContacto.class);
        MensajeContacto mensajeContacto1 = getMensajeContactoSample1();
        MensajeContacto mensajeContacto2 = new MensajeContacto();
        assertThat(mensajeContacto1).isNotEqualTo(mensajeContacto2);

        mensajeContacto2.setId(mensajeContacto1.getId());
        assertThat(mensajeContacto1).isEqualTo(mensajeContacto2);

        mensajeContacto2 = getMensajeContactoSample2();
        assertThat(mensajeContacto1).isNotEqualTo(mensajeContacto2);
    }
}
