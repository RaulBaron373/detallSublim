import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ISolicitudPresupuesto, NewSolicitudPresupuesto } from '../solicitud-presupuesto.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISolicitudPresupuesto for edit and NewSolicitudPresupuestoFormGroupInput for create.
 */
type SolicitudPresupuestoFormGroupInput = ISolicitudPresupuesto | PartialWithRequiredKeyOf<NewSolicitudPresupuesto>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ISolicitudPresupuesto | NewSolicitudPresupuesto> = Omit<T, 'fechaSolicitud' | 'fechaEnvioPresupuesto'> & {
  fechaSolicitud?: string | null;
  fechaEnvioPresupuesto?: string | null;
};

type SolicitudPresupuestoFormRawValue = FormValueOf<ISolicitudPresupuesto>;

type NewSolicitudPresupuestoFormRawValue = FormValueOf<NewSolicitudPresupuesto>;

type SolicitudPresupuestoFormDefaults = Pick<NewSolicitudPresupuesto, 'id' | 'fechaSolicitud'>;

type SolicitudPresupuestoFormGroupContent = {
  id: FormControl<SolicitudPresupuestoFormRawValue['id'] | NewSolicitudPresupuesto['id']>;
  nombreCliente: FormControl<SolicitudPresupuestoFormRawValue['nombreCliente']>;
  email: FormControl<SolicitudPresupuestoFormRawValue['email']>;
  telefono: FormControl<SolicitudPresupuestoFormRawValue['telefono']>;
  nombreEmpresa: FormControl<SolicitudPresupuestoFormRawValue['nombreEmpresa']>;
  descripcion: FormControl<SolicitudPresupuestoFormRawValue['descripcion']>;
  cantidad: FormControl<SolicitudPresupuestoFormRawValue['cantidad']>;
  fechaSolicitud: FormControl<SolicitudPresupuestoFormRawValue['fechaSolicitud']>;
  estado: FormControl<SolicitudPresupuestoFormRawValue['estado']>;
  observacionesInternas: FormControl<SolicitudPresupuestoFormRawValue['observacionesInternas']>;
  precioPresupuesto: FormControl<SolicitudPresupuestoFormRawValue['precioPresupuesto']>;
  tiempoEstimado: FormControl<SolicitudPresupuestoFormRawValue['tiempoEstimado']>;
  observacionesPresupuesto: FormControl<SolicitudPresupuestoFormRawValue['observacionesPresupuesto']>;
  fechaEnvioPresupuesto: FormControl<SolicitudPresupuestoFormRawValue['fechaEnvioPresupuesto']>;
  producto: FormControl<SolicitudPresupuestoFormRawValue['producto']>;
};

export type SolicitudPresupuestoFormGroup = FormGroup<SolicitudPresupuestoFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SolicitudPresupuestoFormService {
  createSolicitudPresupuestoFormGroup(
    solicitudPresupuesto: SolicitudPresupuestoFormGroupInput = { id: null },
  ): SolicitudPresupuestoFormGroup {
    const solicitudPresupuestoRawValue = this.convertSolicitudPresupuestoToSolicitudPresupuestoRawValue({
      ...this.getFormDefaults(),
      ...solicitudPresupuesto,
    });
    return new FormGroup<SolicitudPresupuestoFormGroupContent>({
      id: new FormControl(
        { value: solicitudPresupuestoRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nombreCliente: new FormControl(solicitudPresupuestoRawValue.nombreCliente, {
        validators: [Validators.required],
      }),
      email: new FormControl(solicitudPresupuestoRawValue.email, {
        validators: [Validators.required],
      }),
      telefono: new FormControl(solicitudPresupuestoRawValue.telefono),
      nombreEmpresa: new FormControl(solicitudPresupuestoRawValue.nombreEmpresa),
      descripcion: new FormControl(solicitudPresupuestoRawValue.descripcion, {
        validators: [Validators.required],
      }),
      cantidad: new FormControl(solicitudPresupuestoRawValue.cantidad, {
        validators: [Validators.required, Validators.min(1)],
      }),
      fechaSolicitud: new FormControl(solicitudPresupuestoRawValue.fechaSolicitud, {
        validators: [Validators.required],
      }),
      estado: new FormControl(solicitudPresupuestoRawValue.estado, {
        validators: [Validators.required],
      }),
      observacionesInternas: new FormControl(solicitudPresupuestoRawValue.observacionesInternas),
      producto: new FormControl(solicitudPresupuestoRawValue.producto),
      precioPresupuesto: new FormControl(solicitudPresupuestoRawValue.precioPresupuesto),
      tiempoEstimado: new FormControl(solicitudPresupuestoRawValue.tiempoEstimado),
      observacionesPresupuesto: new FormControl(solicitudPresupuestoRawValue.observacionesPresupuesto),
      fechaEnvioPresupuesto: new FormControl(solicitudPresupuestoRawValue.fechaEnvioPresupuesto),
    });
  }

  getSolicitudPresupuesto(form: SolicitudPresupuestoFormGroup): ISolicitudPresupuesto | NewSolicitudPresupuesto {
    return this.convertSolicitudPresupuestoRawValueToSolicitudPresupuesto(
      form.getRawValue() as SolicitudPresupuestoFormRawValue | NewSolicitudPresupuestoFormRawValue,
    );
  }

  resetForm(form: SolicitudPresupuestoFormGroup, solicitudPresupuesto: SolicitudPresupuestoFormGroupInput): void {
    const solicitudPresupuestoRawValue = this.convertSolicitudPresupuestoToSolicitudPresupuestoRawValue({
      ...this.getFormDefaults(),
      ...solicitudPresupuesto,
    });
    form.reset(
      {
        ...solicitudPresupuestoRawValue,
        id: { value: solicitudPresupuestoRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): SolicitudPresupuestoFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      fechaSolicitud: currentTime,
    };
  }

  private convertSolicitudPresupuestoRawValueToSolicitudPresupuesto(
    rawSolicitudPresupuesto: SolicitudPresupuestoFormRawValue | NewSolicitudPresupuestoFormRawValue,
  ): ISolicitudPresupuesto | NewSolicitudPresupuesto {
    return {
      ...rawSolicitudPresupuesto,
      fechaSolicitud: dayjs(rawSolicitudPresupuesto.fechaSolicitud, DATE_TIME_FORMAT),
      fechaEnvioPresupuesto: rawSolicitudPresupuesto.fechaEnvioPresupuesto
        ? dayjs(rawSolicitudPresupuesto.fechaEnvioPresupuesto, DATE_TIME_FORMAT)
        : null,
    };
  }

  private convertSolicitudPresupuestoToSolicitudPresupuestoRawValue(
    solicitudPresupuesto: ISolicitudPresupuesto | (Partial<NewSolicitudPresupuesto> & SolicitudPresupuestoFormDefaults),
  ): SolicitudPresupuestoFormRawValue | PartialWithRequiredKeyOf<NewSolicitudPresupuestoFormRawValue> {
    return {
      ...solicitudPresupuesto,
      fechaSolicitud: solicitudPresupuesto.fechaSolicitud ? solicitudPresupuesto.fechaSolicitud.format(DATE_TIME_FORMAT) : undefined,
      fechaEnvioPresupuesto: solicitudPresupuesto.fechaEnvioPresupuesto
        ? solicitudPresupuesto.fechaEnvioPresupuesto.format(DATE_TIME_FORMAT)
        : undefined,
    };
  }
}
