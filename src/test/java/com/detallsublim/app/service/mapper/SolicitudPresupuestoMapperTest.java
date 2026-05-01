package com.detallsublim.app.service.mapper;

import static com.detallsublim.app.domain.SolicitudPresupuestoAsserts.*;
import static com.detallsublim.app.domain.SolicitudPresupuestoTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SolicitudPresupuestoMapperTest {

    private SolicitudPresupuestoMapper solicitudPresupuestoMapper;

    @BeforeEach
    void setUp() {
        solicitudPresupuestoMapper = new SolicitudPresupuestoMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSolicitudPresupuestoSample1();
        var actual = solicitudPresupuestoMapper.toEntity(solicitudPresupuestoMapper.toDto(expected));
        assertSolicitudPresupuestoAllPropertiesEquals(expected, actual);
    }
}
