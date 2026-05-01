package com.detallsublim.app.service.mapper;

import com.detallsublim.app.domain.MensajeContacto;
import com.detallsublim.app.service.dto.MensajeContactoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MensajeContacto} and its DTO {@link MensajeContactoDTO}.
 */
@Mapper(componentModel = "spring")
public interface MensajeContactoMapper extends EntityMapper<MensajeContactoDTO, MensajeContacto> {}
