package com.detallsublim.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ProductoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Producto getProductoSample1() {
        return new Producto().id(1L).nombre("nombre1").plazoEstimadoDias(1).imagenUrl("imagenUrl1");
    }

    public static Producto getProductoSample2() {
        return new Producto().id(2L).nombre("nombre2").plazoEstimadoDias(2).imagenUrl("imagenUrl2");
    }

    public static Producto getProductoRandomSampleGenerator() {
        return new Producto()
            .id(longCount.incrementAndGet())
            .nombre(UUID.randomUUID().toString())
            .plazoEstimadoDias(intCount.incrementAndGet())
            .imagenUrl(UUID.randomUUID().toString());
    }
}
