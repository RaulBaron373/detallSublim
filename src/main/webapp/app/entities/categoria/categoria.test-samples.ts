import { ICategoria, NewCategoria } from './categoria.model';

export const sampleWithRequiredData: ICategoria = {
  id: 16672,
  nombre: 'clavicle',
  activa: false,
};

export const sampleWithPartialData: ICategoria = {
  id: 25718,
  nombre: 'tough ouch while',
  activa: true,
};

export const sampleWithFullData: ICategoria = {
  id: 12751,
  nombre: 'what unlucky yowza',
  descripcion: '../fake-data/blob/hipster.txt',
  activa: true,
};

export const sampleWithNewData: NewCategoria = {
  nombre: 'unbalance delightfully like',
  activa: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
