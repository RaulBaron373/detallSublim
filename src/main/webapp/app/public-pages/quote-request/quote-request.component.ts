import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';

import { PublicCatalogService } from 'app/core/catalog/public-catalog.service';
import { IProducto } from 'app/entities/producto/producto.model';
import SharedModule from 'app/shared/shared.module';

@Component({
  selector: 'jhi-quote-request',
  standalone: true,
  imports: [FormsModule, SharedModule],
  templateUrl: './quote-request.component.html',
  styleUrl: './quote-request.component.scss',
})
export class QuoteRequestComponent implements OnInit {
  form = {
    nombreCliente: '',
    email: '',
    telefono: '',
    nombreEmpresa: '',
    producto: '',
    descripcion: '',
    cantidad: 1,
  };

  successMessage = '';
  errorMessage = '';

  productos: IProducto[] = [];

  constructor(
    private readonly http: HttpClient,
    private readonly publicCatalogService: PublicCatalogService,
    private readonly route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.publicCatalogService.getProductos().subscribe(productos => {
      this.productos = productos;

      this.route.queryParams.subscribe(params => {
        const productoId = Number(params['producto']);

        if (productoId) {
          this.form.producto = productoId.toString();
        }
      });
    });
  }

  enviarSolicitud(): void {
    this.successMessage = '';
    this.errorMessage = '';

    if (!this.form.nombreCliente.trim() || !this.form.email.trim() || !this.form.telefono.trim() || !this.form.descripcion.trim()) {
      this.errorMessage = 'Debes completar todos los campos obligatorios.';

      return;
    }

    const payload = {
      nombreCliente: this.form.nombreCliente,

      email: this.form.email,

      telefono: this.form.telefono,

      nombreEmpresa: this.form.nombreEmpresa,

      descripcion: this.form.descripcion,

      cantidad: Number(this.form.cantidad),

      productoId: this.form.producto ? Number(this.form.producto) : null,
    };

    this.http.post<unknown>('/api/public/quote-request', payload).subscribe({
      next: () => {
        this.successMessage = 'Solicitud enviada correctamente.';

        this.form = {
          nombreCliente: '',
          email: '',
          telefono: '',
          nombreEmpresa: '',
          producto: '',
          descripcion: '',
          cantidad: 1,
        };
      },

      error: () => {
        this.errorMessage = 'Ha ocurrido un error al enviar la solicitud.';
      },
    });
  }
}
