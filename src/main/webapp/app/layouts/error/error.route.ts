import { Routes } from '@angular/router';

export const errorRoute: Routes = [
  {
    path: 'error',

    loadComponent: () => import('./error.component'),

    title: 'Error',

    data: {
      statusCode: '500',

      eyebrow: 'ERROR DEL SISTEMA',

      heading: 'Algo no ha salido como esperábamos',

      description: 'Ha ocurrido un error inesperado. Puedes volver al inicio e intentarlo de nuevo.',
    },
  },

  {
    path: 'accessdenied',

    loadComponent: () => import('./error.component'),

    title: 'Acceso restringido',

    data: {
      statusCode: '403',

      eyebrow: 'ACCESO RESTRINGIDO',

      heading: 'Este espacio está restringido',

      description: 'No tienes permisos para acceder a esta sección de Detall Sublim.',
    },
  },

  {
    path: '404',

    loadComponent: () => import('./error.component'),

    title: 'Página no encontrada',

    data: {
      statusCode: '404',

      eyebrow: 'PÁGINA NO ENCONTRADA',

      heading: 'Parece que este detalle se nos ha escapado',

      description: 'La página que buscas no existe, ha cambiado de lugar o ya no está disponible.',
    },
  },

  {
    path: '**',
    redirectTo: '/404',
  },
];
