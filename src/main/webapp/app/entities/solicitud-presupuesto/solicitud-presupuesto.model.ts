import dayjs from 'dayjs/esm';
import { IProducto } from 'app/entities/producto/producto.model';
import { EstadoSolicitud } from 'app/entities/enumerations/estado-solicitud.model';

export interface ISolicitudPresupuesto {
  id: number;
  nombreCliente?: string | null;
  email?: string | null;
  telefono?: string | null;
  nombreEmpresa?: string | null;
  descripcion?: string | null;
  cantidad?: number | null;
  fechaSolicitud?: dayjs.Dayjs | null;
  estado?: keyof typeof EstadoSolicitud | null;
  observacionesInternas?: string | null;
  producto?: Pick<IProducto, 'id' | 'nombre'> | null;
}

export type NewSolicitudPresupuesto = Omit<ISolicitudPresupuesto, 'id'> & { id: null };
