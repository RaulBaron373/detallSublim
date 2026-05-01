package com.detallsublim.app.service.mapper;

import static com.detallsublim.app.domain.MensajeContactoAsserts.*;
import static com.detallsublim.app.domain.MensajeContactoTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MensajeContactoMapperTest {

    private MensajeContactoMapper mensajeContactoMapper;

    @BeforeEach
    void setUp() {
        mensajeContactoMapper = new MensajeContactoMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMensajeContactoSample1();
        var actual = mensajeContactoMapper.toEntity(mensajeContactoMapper.toDto(expected));
        assertMensajeContactoAllPropertiesEquals(expected, actual);
    }
}
