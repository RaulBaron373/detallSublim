import dayjs from 'dayjs/esm';

export interface IMensajeContacto {
  id: number;
  nombre?: string | null;
  email?: string | null;
  telefono?: string | null;
  asunto?: string | null;
  mensaje?: string | null;
  fechaEnvio?: dayjs.Dayjs | null;
  atendido?: boolean | null;
}

export type NewMensajeContacto = Omit<IMensajeContacto, 'id'> & { id: null };
