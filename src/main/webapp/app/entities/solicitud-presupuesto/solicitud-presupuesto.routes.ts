import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import SolicitudPresupuestoResolve from './route/solicitud-presupuesto-routing-resolve.service';
import { Authority } from 'app/config/authority.constants';

const solicitudPresupuestoRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/solicitud-presupuesto.component').then(m => m.SolicitudPresupuestoComponent),
    data: {
      defaultSort: `id,${ASC}`,
      authorities: [Authority.ADMIN, Authority.VIEWER, Authority.USER],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/solicitud-presupuesto-detail.component').then(m => m.SolicitudPresupuestoDetailComponent),
    resolve: {
      solicitudPresupuesto: SolicitudPresupuestoResolve,
    },
    data: {
      authorities: [Authority.ADMIN, Authority.VIEWER, Authority.USER],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/solicitud-presupuesto-update.component').then(m => m.SolicitudPresupuestoUpdateComponent),
    resolve: {
      solicitudPresupuesto: SolicitudPresupuestoResolve,
    },
    data: {
      authorities: [Authority.ADMIN, Authority.USER],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/solicitud-presupuesto-update.component').then(m => m.SolicitudPresupuestoUpdateComponent),
    resolve: {
      solicitudPresupuesto: SolicitudPresupuestoResolve,
    },
    data: {
      authorities: [Authority.ADMIN, Authority.USER],
    },
    canActivate: [UserRouteAccessService],
  },
];

export default solicitudPresupuestoRoute;
