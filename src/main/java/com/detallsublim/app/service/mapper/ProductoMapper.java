package com.detallsublim.app.service.mapper;

import com.detallsublim.app.domain.Categoria;
import com.detallsublim.app.domain.Producto;
import com.detallsublim.app.service.dto.CategoriaDTO;
import com.detallsublim.app.service.dto.ProductoDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductoMapper extends EntityMapper<ProductoDTO, Producto> {
    @Mapping(target = "categoria", source = "categoria", qualifiedByName = "categoriaNombre")
    ProductoDTO toDto(Producto s);

    @Named("categoriaNombre")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    @Mapping(target = "activa", source = "activa")
    CategoriaDTO toDtoCategoriaNombre(Categoria categoria);
}
