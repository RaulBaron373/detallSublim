import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SolicitudPresupuestoService } from 'app/entities/solicitud-presupuesto/service/solicitud-presupuesto.service';
import dayjs from 'dayjs/esm';
import { EstadoSolicitud } from 'app/entities/enumerations/estado-solicitud.model';
import { NewSolicitudPresupuesto } from 'app/entities/solicitud-presupuesto/solicitud-presupuesto.model';
import { ActivatedRoute } from '@angular/router';
import { ProductoService } from 'app/entities/producto/service/producto.service';
import { IProducto } from 'app/entities/producto/producto.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'jhi-quote-request',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './quote-request.component.html',
  styleUrl: './quote-request.component.scss',
})
export class QuoteRequestComponent {
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
    private solicitudPresupuestoService: SolicitudPresupuestoService,
    private productoService: ProductoService,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.productoService.query().subscribe(res => {
      this.productos = (res.body ?? []).filter(producto => producto.activo);

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

    const productoSeleccionado = this.productos.find(producto => producto.id === Number(this.form.producto)) ?? null;

    const payload: NewSolicitudPresupuesto = {
      id: null,
      nombreCliente: this.form.nombreCliente,
      email: this.form.email,
      telefono: this.form.telefono,
      nombreEmpresa: this.form.nombreEmpresa,
      descripcion: this.form.descripcion,
      cantidad: Number(this.form.cantidad),
      fechaSolicitud: dayjs(),
      estado: EstadoSolicitud.PENDIENTE,
      producto: productoSeleccionado,
    };

    this.solicitudPresupuestoService.create(payload).subscribe({
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
