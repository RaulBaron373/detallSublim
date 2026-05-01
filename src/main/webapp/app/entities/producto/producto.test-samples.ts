import { IProducto, NewProducto } from './producto.model';

export const sampleWithRequiredData: IProducto = {
  id: 26142,
  nombre: 'till',
  precioBase: 21121.4,
  personalizable: true,
  activo: false,
};

export const sampleWithPartialData: IProducto = {
  id: 25274,
  nombre: 'slope fashion',
  precioBase: 28795.71,
  personalizable: true,
  plazoEstimadoDias: 29395,
  imagenUrl: 'glum boo partial',
  activo: true,
};

export const sampleWithFullData: IProducto = {
  id: 31795,
  nombre: 'er behold needily',
  descripcion: '../fake-data/blob/hipster.txt',
  precioBase: 12411.58,
  personalizable: false,
  plazoEstimadoDias: 25929,
  imagenUrl: 'ring right',
  activo: true,
};

export const sampleWithNewData: NewProducto = {
  nombre: 'perfection highly quickly',
  precioBase: 30196.45,
  personalizable: false,
  activo: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
