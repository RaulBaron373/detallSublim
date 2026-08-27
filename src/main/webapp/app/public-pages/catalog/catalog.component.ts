import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IProducto } from 'app/entities/producto/producto.model';
import { ICategoria } from 'app/entities/categoria/categoria.model';
import { PublicCatalogService } from 'app/core/catalog/public-catalog.service';

@Component({
  selector: 'jhi-catalog',
  standalone: true,
  imports: [SharedModule, RouterLink],
  templateUrl: './catalog.component.html',
  styleUrl: './catalog.component.scss',
})
export class CatalogComponent implements OnInit {
  productos: IProducto[] = [];
  productosFiltrados: IProducto[] = [];
  categorias: ICategoria[] = [];
  categoriaSeleccionada = 'Todos';

  constructor(private readonly publicCatalogService: PublicCatalogService) {}

  ngOnInit(): void {
    this.cargarProductos();
    this.cargarCategorias();
  }

  cargarProductos(): void {
    this.publicCatalogService.getProductos().subscribe(productos => {
      this.productos = productos;

      this.productosFiltrados = [...productos];
    });
  }

  cargarCategorias(): void {
    this.publicCatalogService.getCategorias().subscribe(categorias => {
      this.categorias = categorias;
    });
  }

  filtrarProductos(nombreCategoria: string): void {
    this.categoriaSeleccionada = nombreCategoria;

    if (nombreCategoria === 'Todos') {
      this.productosFiltrados = [...this.productos];

      return;
    }

    this.productosFiltrados = this.productos.filter(producto => producto.categoria?.nombre === nombreCategoria);
  }
}
