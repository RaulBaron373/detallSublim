import { ICategoria } from 'app/entities/categoria/categoria.model';

export interface IProducto {
  id: number;
  nombre?: string | null;
  descripcion?: string | null;
  precioBase?: number | null;
  personalizable?: boolean | null;
  plazoEstimadoDias?: number | null;
  imagenUrl?: string | null;
  activo?: boolean | null;
  categoria?: Pick<ICategoria, 'id' | 'nombre'> | null;
}

export type NewProducto = Omit<IProducto, 'id'> & { id: null };
