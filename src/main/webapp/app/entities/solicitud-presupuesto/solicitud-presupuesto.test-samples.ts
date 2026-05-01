import dayjs from 'dayjs/esm';

import { ISolicitudPresupuesto, NewSolicitudPresupuesto } from './solicitud-presupuesto.model';

export const sampleWithRequiredData: ISolicitudPresupuesto = {
  id: 19189,
  nombreCliente: 'ordinary',
  email: 'Claudio_BacaZuniga@yahoo.com',
  descripcion: '../fake-data/blob/hipster.txt',
  cantidad: 7756,
  fechaSolicitud: dayjs('2026-04-14T15:28'),
  estado: 'EN_PRODUCCION',
};

export const sampleWithPartialData: ISolicitudPresupuesto = {
  id: 5958,
  nombreCliente: 'gadzooks heavily dense',
  email: 'Antonia97@hotmail.com',
  nombreEmpresa: 'above',
  descripcion: '../fake-data/blob/hipster.txt',
  cantidad: 2040,
  fechaSolicitud: dayjs('2026-04-14T00:57'),
  estado: 'FINALIZADO',
};

export const sampleWithFullData: ISolicitudPresupuesto = {
  id: 995,
  nombreCliente: 'cruelly starch',
  email: 'Manuel30@gmail.com',
  telefono: 'oh furthermore before',
  nombreEmpresa: 'fortunately humidity till',
  descripcion: '../fake-data/blob/hipster.txt',
  cantidad: 20937,
  fechaSolicitud: dayjs('2026-04-13T19:13'),
  estado: 'EN_REVISION',
  observacionesInternas: '../fake-data/blob/hipster.txt',
};

export const sampleWithNewData: NewSolicitudPresupuesto = {
  nombreCliente: 'defiantly',
  email: 'Debora_OrtizAlejandro@yahoo.com',
  descripcion: '../fake-data/blob/hipster.txt',
  cantidad: 11115,
  fechaSolicitud: dayjs('2026-04-13T17:22'),
  estado: 'ACEPTADO',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
