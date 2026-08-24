import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IProducto, NewProducto } from '../producto.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProducto for edit and NewProductoFormGroupInput for create.
 */
type ProductoFormGroupInput = IProducto | PartialWithRequiredKeyOf<NewProducto>;

type ProductoFormDefaults = Pick<NewProducto, 'id' | 'personalizable' | 'activo' | 'destacado'>;

type ProductoFormGroupContent = {
  id: FormControl<IProducto['id'] | NewProducto['id']>;
  nombre: FormControl<IProducto['nombre']>;
  descripcion: FormControl<IProducto['descripcion']>;
  precioBase: FormControl<IProducto['precioBase']>;
  personalizable: FormControl<IProducto['personalizable']>;
  plazoEstimadoDias: FormControl<IProducto['plazoEstimadoDias']>;
  imagenUrl: FormControl<IProducto['imagenUrl']>;
  activo: FormControl<IProducto['activo']>;
  categoria: FormControl<IProducto['categoria']>;
  destacado: FormControl<IProducto['destacado']>;
};

export type ProductoFormGroup = FormGroup<ProductoFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProductoFormService {
  createProductoFormGroup(producto: ProductoFormGroupInput = { id: null }): ProductoFormGroup {
    const productoRawValue = {
      ...this.getFormDefaults(),
      ...producto,
    };
    return new FormGroup<ProductoFormGroupContent>({
      id: new FormControl(
        { value: productoRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nombre: new FormControl(productoRawValue.nombre, {
        validators: [Validators.required],
      }),
      descripcion: new FormControl(productoRawValue.descripcion),
      precioBase: new FormControl(productoRawValue.precioBase, {
        validators: [Validators.required, Validators.min(0)],
      }),
      personalizable: new FormControl(productoRawValue.personalizable, {
        validators: [Validators.required],
      }),
      plazoEstimadoDias: new FormControl(productoRawValue.plazoEstimadoDias),
      imagenUrl: new FormControl(productoRawValue.imagenUrl),
      activo: new FormControl(productoRawValue.activo, {
        validators: [Validators.required],
      }),
      categoria: new FormControl(productoRawValue.categoria),
      destacado: new FormControl(productoRawValue.destacado, {
        validators: [Validators.required],
      }),
    });
  }

  getProducto(form: ProductoFormGroup): IProducto | NewProducto {
    return form.getRawValue() as IProducto | NewProducto;
  }

  resetForm(form: ProductoFormGroup, producto: ProductoFormGroupInput): void {
    const productoRawValue = { ...this.getFormDefaults(), ...producto };
    form.reset(
      {
        ...productoRawValue,
        id: { value: productoRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ProductoFormDefaults {
    return {
      id: null,
      personalizable: false,
      activo: false,
      destacado: false,
    };
  }
}
