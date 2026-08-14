import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ProductoService } from 'app/entities/producto/service/producto.service';
import { IProducto } from 'app/entities/producto/producto.model';
import { CategoriaService } from 'app/entities/categoria/service/categoria.service';
import { ICategoria } from 'app/entities/categoria/categoria.model';

@Component({
  selector: 'jhi-catalog',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './catalog.component.html',
  styleUrl: './catalog.component.scss',
})
export class CatalogComponent implements OnInit {
  productos: IProducto[] = [];
  productosFiltrados: IProducto[] = [];
  categorias: ICategoria[] = [];
  categoriaSeleccionada = 'Todos';

  constructor(
    private productoService: ProductoService,
    private categoriaService: CategoriaService,
  ) {}

  ngOnInit(): void {
    this.cargarProductos();
    this.cargarCategorias();
  }

  cargarProductos(): void {
    this.productoService.query().subscribe(res => {
      this.productos = (res.body ?? []).filter(producto => producto.activo);

      this.productosFiltrados = [...this.productos];
    });
  }

  cargarCategorias(): void {
    this.categoriaService.query().subscribe(res => {
      this.categorias = res.body ?? [];
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
