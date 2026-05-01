package com.detallsublim.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MensajeContactoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static MensajeContacto getMensajeContactoSample1() {
        return new MensajeContacto().id(1L).nombre("nombre1").email("email1").telefono("telefono1").asunto("asunto1");
    }

    public static MensajeContacto getMensajeContactoSample2() {
        return new MensajeContacto().id(2L).nombre("nombre2").email("email2").telefono("telefono2").asunto("asunto2");
    }

    public static MensajeContacto getMensajeContactoRandomSampleGenerator() {
        return new MensajeContacto()
            .id(longCount.incrementAndGet())
            .nombre(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .telefono(UUID.randomUUID().toString())
            .asunto(UUID.randomUUID().toString());
    }
}
