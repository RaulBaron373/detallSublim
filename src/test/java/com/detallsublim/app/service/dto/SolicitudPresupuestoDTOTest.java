package com.detallsublim.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.detallsublim.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SolicitudPresupuestoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SolicitudPresupuestoDTO.class);
        SolicitudPresupuestoDTO solicitudPresupuestoDTO1 = new SolicitudPresupuestoDTO();
        solicitudPresupuestoDTO1.setId(1L);
        SolicitudPresupuestoDTO solicitudPresupuestoDTO2 = new SolicitudPresupuestoDTO();
        assertThat(solicitudPresupuestoDTO1).isNotEqualTo(solicitudPresupuestoDTO2);
        solicitudPresupuestoDTO2.setId(solicitudPresupuestoDTO1.getId());
        assertThat(solicitudPresupuestoDTO1).isEqualTo(solicitudPresupuestoDTO2);
        solicitudPresupuestoDTO2.setId(2L);
        assertThat(solicitudPresupuestoDTO1).isNotEqualTo(solicitudPresupuestoDTO2);
        solicitudPresupuestoDTO1.setId(null);
        assertThat(solicitudPresupuestoDTO1).isNotEqualTo(solicitudPresupuestoDTO2);
    }
}
