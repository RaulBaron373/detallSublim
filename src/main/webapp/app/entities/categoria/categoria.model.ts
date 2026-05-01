export interface ICategoria {
  id: number;
  nombre?: string | null;
  descripcion?: string | null;
  activa?: boolean | null;
}

export type NewCategoria = Omit<ICategoria, 'id'> & { id: null };
