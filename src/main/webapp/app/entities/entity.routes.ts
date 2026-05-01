import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'detallSublimApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'categoria',
    data: { pageTitle: 'detallSublimApp.categoria.home.title' },
    loadChildren: () => import('./categoria/categoria.routes'),
  },
  {
    path: 'producto',
    data: { pageTitle: 'detallSublimApp.producto.home.title' },
    loadChildren: () => import('./producto/producto.routes'),
  },
  {
    path: 'solicitud-presupuesto',
    data: { pageTitle: 'detallSublimApp.solicitudPresupuesto.home.title' },
    loadChildren: () => import('./solicitud-presupuesto/solicitud-presupuesto.routes'),
  },
  {
    path: 'mensaje-contacto',
    data: { pageTitle: 'detallSublimApp.mensajeContacto.home.title' },
    loadChildren: () => import('./mensaje-contacto/mensaje-contacto.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
