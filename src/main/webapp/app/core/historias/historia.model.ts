export type EstadoHistoria = 'BORRADOR' | 'PUBLICADA';

export interface IHistoriaEditable {
  titulo: string;
  resumen: string;
  contenido: string;
}

export interface IHistoriaImagen {
  id: number;
  textoAlternativo: string | null;
  orden: number;
  portada: boolean;
  url: string | null;
}

export interface IHistoriaResumen {
  id: number;
  titulo: string;
  slug: string;
  resumen: string;
  fechaPublicacion: string | null;
  portadaId: number | null;
  portadaTextoAlternativo: string | null;
  portadaUrl: string | null;
}

export interface IHistoria {
  id: number;
  titulo: string;
  slug: string;
  resumen: string;
  contenido: string;
  estado: EstadoHistoria;
  fechaCreacion: string | null;
  fechaActualizacion: string | null;
  fechaPublicacion: string | null;
  imagenes: IHistoriaImagen[];
}
