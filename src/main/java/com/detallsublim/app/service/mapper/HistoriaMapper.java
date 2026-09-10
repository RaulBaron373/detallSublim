package com.detallsublim.app.service.mapper;

import com.detallsublim.app.domain.Historia;
import com.detallsublim.app.service.dto.HistoriaDTO;
import org.mapstruct.Mapper;

/**
 * Mapper de solo lectura para Historia.
 *
 * Los campos controlados por el backend, como slug, estado y fechas,
 * se gestionan exclusivamente desde la capa de servicio.
 */
@Mapper(componentModel = "spring", uses = { HistoriaImagenMapper.class })
public interface HistoriaMapper {
    HistoriaDTO toDto(Historia historia);
}
