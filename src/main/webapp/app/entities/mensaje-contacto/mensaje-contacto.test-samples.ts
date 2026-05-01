import dayjs from 'dayjs/esm';

import { IMensajeContacto, NewMensajeContacto } from './mensaje-contacto.model';

export const sampleWithRequiredData: IMensajeContacto = {
  id: 31233,
  nombre: 'when',
  email: 'Marta_AlmanzaAdorno@gmail.com',
  asunto: 'hollow',
  mensaje: '../fake-data/blob/hipster.txt',
  fechaEnvio: dayjs('2026-04-13T21:28'),
  atendido: false,
};

export const sampleWithPartialData: IMensajeContacto = {
  id: 12055,
  nombre: 'repentant awful micromanage',
  email: 'Blanca.BalderasMarquez@yahoo.com',
  telefono: 'store actual instance',
  asunto: 'yesterday',
  mensaje: '../fake-data/blob/hipster.txt',
  fechaEnvio: dayjs('2026-04-14T11:33'),
  atendido: false,
};

export const sampleWithFullData: IMensajeContacto = {
  id: 27384,
  nombre: 'unless morning er',
  email: 'Benito5@yahoo.com',
  telefono: 'round drat',
  asunto: 'though',
  mensaje: '../fake-data/blob/hipster.txt',
  fechaEnvio: dayjs('2026-04-14T12:28'),
  atendido: false,
};

export const sampleWithNewData: NewMensajeContacto = {
  nombre: 'amid vista',
  email: 'MariaLuisa_CalderonToro@hotmail.com',
  asunto: 'mortar usually slimy',
  mensaje: '../fake-data/blob/hipster.txt',
  fechaEnvio: dayjs('2026-04-14T08:08'),
  atendido: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
