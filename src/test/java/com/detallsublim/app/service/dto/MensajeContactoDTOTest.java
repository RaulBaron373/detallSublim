package com.detallsublim.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.detallsublim.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MensajeContactoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MensajeContactoDTO.class);
        MensajeContactoDTO mensajeContactoDTO1 = new MensajeContactoDTO();
        mensajeContactoDTO1.setId(1L);
        MensajeContactoDTO mensajeContactoDTO2 = new MensajeContactoDTO();
        assertThat(mensajeContactoDTO1).isNotEqualTo(mensajeContactoDTO2);
        mensajeContactoDTO2.setId(mensajeContactoDTO1.getId());
        assertThat(mensajeContactoDTO1).isEqualTo(mensajeContactoDTO2);
        mensajeContactoDTO2.setId(2L);
        assertThat(mensajeContactoDTO1).isNotEqualTo(mensajeContactoDTO2);
        mensajeContactoDTO1.setId(null);
        assertThat(mensajeContactoDTO1).isNotEqualTo(mensajeContactoDTO2);
    }
}
