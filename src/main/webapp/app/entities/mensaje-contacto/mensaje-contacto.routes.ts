import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import MensajeContactoResolve from './route/mensaje-contacto-routing-resolve.service';
import { Authority } from 'app/config/authority.constants';

const mensajeContactoRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/mensaje-contacto.component').then(m => m.MensajeContactoComponent),
    data: {
      defaultSort: `id,${ASC}`,
      authorities: [Authority.ADMIN, Authority.VIEWER, Authority.USER],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/mensaje-contacto-detail.component').then(m => m.MensajeContactoDetailComponent),
    resolve: {
      mensajeContacto: MensajeContactoResolve,
    },
    data: {
      authorities: [Authority.ADMIN, Authority.VIEWER, Authority.USER],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/mensaje-contacto-update.component').then(m => m.MensajeContactoUpdateComponent),
    resolve: {
      mensajeContacto: MensajeContactoResolve,
    },
    data: {
      authorities: [Authority.ADMIN, Authority.USER],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/mensaje-contacto-update.component').then(m => m.MensajeContactoUpdateComponent),
    resolve: {
      mensajeContacto: MensajeContactoResolve,
    },
    data: {
      authorities: [Authority.ADMIN, Authority.USER],
    },
    canActivate: [UserRouteAccessService],
  },
];

export default mensajeContactoRoute;
