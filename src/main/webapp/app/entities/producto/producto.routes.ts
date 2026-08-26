import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import ProductoResolve from './route/producto-routing-resolve.service';
import { Authority } from 'app/config/authority.constants';

const productoRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/producto.component').then(m => m.ProductoComponent),
    data: {
      defaultSort: `nombre,${ASC}`,
      authorities: [Authority.ADMIN, Authority.VIEWER, Authority.USER],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/producto-detail.component').then(m => m.ProductoDetailComponent),
    resolve: {
      producto: ProductoResolve,
    },
    data: {
      authorities: [Authority.ADMIN, Authority.VIEWER, Authority.USER],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/producto-update.component').then(m => m.ProductoUpdateComponent),
    resolve: {
      producto: ProductoResolve,
    },
    data: {
      authorities: [Authority.ADMIN, Authority.USER],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/producto-update.component').then(m => m.ProductoUpdateComponent),
    resolve: {
      producto: ProductoResolve,
    },
    data: {
      authorities: [Authority.ADMIN, Authority.USER],
    },
    canActivate: [UserRouteAccessService],
  },
];

export default productoRoute;
