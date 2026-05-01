package com.detallsublim.app.service.mapper;

import com.detallsublim.app.domain.Producto;
import com.detallsublim.app.domain.SolicitudPresupuesto;
import com.detallsublim.app.service.dto.ProductoDTO;
import com.detallsublim.app.service.dto.SolicitudPresupuestoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SolicitudPresupuesto} and its DTO {@link SolicitudPresupuestoDTO}.
 */
@Mapper(componentModel = "spring")
public interface SolicitudPresupuestoMapper extends EntityMapper<SolicitudPresupuestoDTO, SolicitudPresupuesto> {
    @Mapping(target = "producto", source = "producto", qualifiedByName = "productoNombre")
    SolicitudPresupuestoDTO toDto(SolicitudPresupuesto s);

    @Named("productoNombre")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    ProductoDTO toDtoProductoNombre(Producto producto);
}
