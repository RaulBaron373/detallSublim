package com.detallsublim.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SolicitudPresupuestoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static SolicitudPresupuesto getSolicitudPresupuestoSample1() {
        return new SolicitudPresupuesto()
            .id(1L)
            .nombreCliente("nombreCliente1")
            .email("email1")
            .telefono("telefono1")
            .nombreEmpresa("nombreEmpresa1")
            .cantidad(1);
    }

    public static SolicitudPresupuesto getSolicitudPresupuestoSample2() {
        return new SolicitudPresupuesto()
            .id(2L)
            .nombreCliente("nombreCliente2")
            .email("email2")
            .telefono("telefono2")
            .nombreEmpresa("nombreEmpresa2")
            .cantidad(2);
    }

    public static SolicitudPresupuesto getSolicitudPresupuestoRandomSampleGenerator() {
        return new SolicitudPresupuesto()
            .id(longCount.incrementAndGet())
            .nombreCliente(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .telefono(UUID.randomUUID().toString())
            .nombreEmpresa(UUID.randomUUID().toString())
            .cantidad(intCount.incrementAndGet());
    }
}
