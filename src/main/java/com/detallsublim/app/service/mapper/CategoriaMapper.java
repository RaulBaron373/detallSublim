package com.detallsublim.app.service.mapper;

import com.detallsublim.app.domain.Categoria;
import com.detallsublim.app.service.dto.CategoriaDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Categoria} and its DTO {@link CategoriaDTO}.
 */
@Mapper(componentModel = "spring")
public interface CategoriaMapper extends EntityMapper<CategoriaDTO, Categoria> {}
