package com.detallsublim.app.web.rest;

import com.detallsublim.app.service.CategoriaService;
import com.detallsublim.app.service.ProductoService;
import com.detallsublim.app.service.dto.CategoriaDTO;
import com.detallsublim.app.service.dto.ProductoDTO;
import java.util.Comparator;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/catalog")
public class PublicCatalogResource {

    private final ProductoService productoService;

    private final CategoriaService categoriaService;

    public PublicCatalogResource(ProductoService productoService, CategoriaService categoriaService) {
        this.productoService = productoService;

        this.categoriaService = categoriaService;
    }

    @GetMapping("/productos")
    public List<ProductoDTO> getProductos() {
        return productoService
            .findAllWithEagerRelationships(Pageable.unpaged())
            .getContent()
            .stream()
            .filter(producto -> Boolean.TRUE.equals(producto.getActivo()))
            /*
             * Si el producto pertenece a una
             * categoría desactivada tampoco
             * debe mostrarse públicamente.
             *
             * Los productos sin categoría
             * siguen siendo válidos.
             */
            .filter(producto -> producto.getCategoria() == null || Boolean.TRUE.equals(producto.getCategoria().getActiva()))
            .sorted(Comparator.comparing(ProductoDTO::getNombre, String.CASE_INSENSITIVE_ORDER))
            .toList();
    }

    @GetMapping("/categorias")
    public List<CategoriaDTO> getCategorias() {
        return categoriaService
            .findAll()
            .stream()
            .filter(categoria -> Boolean.TRUE.equals(categoria.getActiva()))
            .sorted(Comparator.comparing(CategoriaDTO::getNombre, String.CASE_INSENSITIVE_ORDER))
            .toList();
    }
}
