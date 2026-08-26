import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { LoginService } from 'app/login/login.service';
import { SolicitudPresupuestoService } from 'app/entities/solicitud-presupuesto/service/solicitud-presupuesto.service';
import { MensajeContactoService } from 'app/entities/mensaje-contacto/service/mensaje-contacto.service';
import { ProductoService } from 'app/entities/producto/service/producto.service';
import HasAnyAuthorityDirective from 'app/shared/auth/has-any-authority.directive';

@Component({
  selector: 'jhi-panel',
  standalone: true,
  imports: [RouterLink, HasAnyAuthorityDirective],
  templateUrl: './panel.component.html',
  styleUrl: './panel.component.scss',
})
export class PanelComponent implements OnInit {
  solicitudesPendientes = 0;
  mensajesPendientes = 0;
  productosActivos = 0;

  constructor(
    private readonly loginService: LoginService,
    private readonly router: Router,
    private readonly solicitudService: SolicitudPresupuestoService,
    private readonly mensajeService: MensajeContactoService,
    private readonly productoService: ProductoService,
  ) {}

  ngOnInit(): void {
    this.loadStats();
  }

  logout(): void {
    this.loginService.logout();
    this.router.navigate(['/']);
  }

  loadStats(): void {
    this.solicitudService.query().subscribe(res => {
      const data = res.body ?? [];
      this.solicitudesPendientes = data.filter(s => s.estado === 'PENDIENTE').length;
    });

    this.mensajeService.query().subscribe(res => {
      const data = res.body ?? [];
      this.mensajesPendientes = data.filter(m => !m.atendido).length;
    });

    this.productoService.query().subscribe(res => {
      const data = res.body ?? [];
      this.productosActivos = data.filter(p => p.activo).length;
    });
  }
}
