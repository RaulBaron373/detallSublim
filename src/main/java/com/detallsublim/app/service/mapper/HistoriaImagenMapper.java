package com.detallsublim.app.service.mapper;

import com.detallsublim.app.domain.HistoriaImagen;
import com.detallsublim.app.service.dto.HistoriaImagenDTO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper de solo lectura para HistoriaImagen.
 *
 * La creación y modificación de los metadatos internos de almacenamiento
 * se gestiona exclusivamente desde el backend.
 */
@Mapper(componentModel = "spring")
public interface HistoriaImagenMapper {
    @Mapping(target = "url", ignore = true)
    HistoriaImagenDTO toDto(HistoriaImagen historiaImagen);

    @AfterMapping
    default void populateUrl(HistoriaImagen historiaImagen, @MappingTarget HistoriaImagenDTO dto) {
        if (historiaImagen != null && historiaImagen.getId() != null) {
            dto.setUrl("/api/public/historias/imagenes/" + historiaImagen.getId());
        }
    }
}
