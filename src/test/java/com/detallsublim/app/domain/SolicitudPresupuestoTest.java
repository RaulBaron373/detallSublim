package com.detallsublim.app.domain;

import static com.detallsublim.app.domain.ProductoTestSamples.*;
import static com.detallsublim.app.domain.SolicitudPresupuestoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.detallsublim.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SolicitudPresupuestoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SolicitudPresupuesto.class);
        SolicitudPresupuesto solicitudPresupuesto1 = getSolicitudPresupuestoSample1();
        SolicitudPresupuesto solicitudPresupuesto2 = new SolicitudPresupuesto();
        assertThat(solicitudPresupuesto1).isNotEqualTo(solicitudPresupuesto2);

        solicitudPresupuesto2.setId(solicitudPresupuesto1.getId());
        assertThat(solicitudPresupuesto1).isEqualTo(solicitudPresupuesto2);

        solicitudPresupuesto2 = getSolicitudPresupuestoSample2();
        assertThat(solicitudPresupuesto1).isNotEqualTo(solicitudPresupuesto2);
    }

    @Test
    void productoTest() {
        SolicitudPresupuesto solicitudPresupuesto = getSolicitudPresupuestoRandomSampleGenerator();
        Producto productoBack = getProductoRandomSampleGenerator();

        solicitudPresupuesto.setProducto(productoBack);
        assertThat(solicitudPresupuesto.getProducto()).isEqualTo(productoBack);

        solicitudPresupuesto.producto(null);
        assertThat(solicitudPresupuesto.getProducto()).isNull();
    }
}
