import { Routes } from '@angular/router';

import { Authority } from 'app/config/authority.constants';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { errorRoute } from './layouts/error/error.route';

const routes: Routes = [
  {
    path: 'panel',
    loadComponent: () => import('./panel/panel.component').then(m => m.PanelComponent),
    title: 'Panel',
    data: {
      authorities: [Authority.ADMIN, Authority.VIEWER, Authority.USER],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'panel/historias',
    loadComponent: () => import('./panel/historias/historias-admin.component').then(m => m.HistoriasAdminComponent),
    title: 'Gestión de Historias',
    data: {
      authorities: [Authority.ADMIN],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'panel/historias/nueva',
    loadComponent: () => import('./panel/historias/form/historia-form.component').then(m => m.HistoriaFormComponent),
    title: 'Nueva historia',
    data: {
      authorities: [Authority.ADMIN],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'panel/historias/:id/editar',
    loadComponent: () => import('./panel/historias/form/historia-form.component').then(m => m.HistoriaFormComponent),
    title: 'Editar historia',
    data: {
      authorities: [Authority.ADMIN],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: '',
    loadComponent: () => import('./home/home.component'),
    title: 'Inicio',
  },
  {
    path: 'quienes-somos',
    loadComponent: () => import('./public-pages/about/about.component').then(m => m.AboutComponent),
    title: 'Quiénes somos',
  },
  {
    path: 'servicios',
    loadComponent: () => import('./public-pages/services/services.component').then(m => m.ServicesComponent),
    title: 'Servicios',
  },
  {
    path: 'catalogo',
    loadComponent: () => import('./public-pages/catalog/catalog.component').then(m => m.CatalogComponent),
    title: 'Catálogo',
  },
  {
    path: 'tecnologias',
    loadComponent: () => import('./public-pages/technologies/technologies.component').then(m => m.TechnologiesComponent),
    title: 'Tecnologías',
  },
  {
    path: 'contacto',
    loadComponent: () => import('./public-pages/contact/contact.component').then(m => m.ContactComponent),
    title: 'Contacto',
  },
  {
    path: 'solicitar-presupuesto',
    loadComponent: () => import('./public-pages/quote-request/quote-request.component').then(m => m.QuoteRequestComponent),
    title: 'Solicitud de presupuesto',
  },
  {
    path: 'historias',
    loadComponent: () => import('./public-pages/historias/historias.component').then(m => m.HistoriasComponent),
    title: 'Historias',
  },
  {
    path: 'historias/:slug',
    loadComponent: () => import('./public-pages/historias/detail/historia-detail.component').then(m => m.HistoriaDetailComponent),
    title: 'Historia',
  },
  {
    path: '',
    loadComponent: () => import('./layouts/navbar/navbar.component'),
    outlet: 'navbar',
  },
  {
    path: 'admin',
    data: {
      authorities: [Authority.ADMIN],
    },
    canActivate: [UserRouteAccessService],
    loadChildren: () => import('./admin/admin.routes'),
  },
  {
    path: 'account',
    loadChildren: () => import('./account/account.route'),
  },
  {
    path: 'login',
    loadComponent: () => import('./login/login.component'),
    title: 'login.title',
  },
  {
    path: '',
    loadChildren: () => import(`./entities/entity.routes`),
  },
  ...errorRoute,
];

export default routes;
