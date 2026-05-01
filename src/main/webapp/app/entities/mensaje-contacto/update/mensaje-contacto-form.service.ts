import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IMensajeContacto, NewMensajeContacto } from '../mensaje-contacto.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMensajeContacto for edit and NewMensajeContactoFormGroupInput for create.
 */
type MensajeContactoFormGroupInput = IMensajeContacto | PartialWithRequiredKeyOf<NewMensajeContacto>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IMensajeContacto | NewMensajeContacto> = Omit<T, 'fechaEnvio'> & {
  fechaEnvio?: string | null;
};

type MensajeContactoFormRawValue = FormValueOf<IMensajeContacto>;

type NewMensajeContactoFormRawValue = FormValueOf<NewMensajeContacto>;

type MensajeContactoFormDefaults = Pick<NewMensajeContacto, 'id' | 'fechaEnvio' | 'atendido'>;

type MensajeContactoFormGroupContent = {
  id: FormControl<MensajeContactoFormRawValue['id'] | NewMensajeContacto['id']>;
  nombre: FormControl<MensajeContactoFormRawValue['nombre']>;
  email: FormControl<MensajeContactoFormRawValue['email']>;
  telefono: FormControl<MensajeContactoFormRawValue['telefono']>;
  asunto: FormControl<MensajeContactoFormRawValue['asunto']>;
  mensaje: FormControl<MensajeContactoFormRawValue['mensaje']>;
  fechaEnvio: FormControl<MensajeContactoFormRawValue['fechaEnvio']>;
  atendido: FormControl<MensajeContactoFormRawValue['atendido']>;
};

export type MensajeContactoFormGroup = FormGroup<MensajeContactoFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MensajeContactoFormService {
  createMensajeContactoFormGroup(mensajeContacto: MensajeContactoFormGroupInput = { id: null }): MensajeContactoFormGroup {
    const mensajeContactoRawValue = this.convertMensajeContactoToMensajeContactoRawValue({
      ...this.getFormDefaults(),
      ...mensajeContacto,
    });
    return new FormGroup<MensajeContactoFormGroupContent>({
      id: new FormControl(
        { value: mensajeContactoRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nombre: new FormControl(mensajeContactoRawValue.nombre, {
        validators: [Validators.required],
      }),
      email: new FormControl(mensajeContactoRawValue.email, {
        validators: [Validators.required],
      }),
      telefono: new FormControl(mensajeContactoRawValue.telefono),
      asunto: new FormControl(mensajeContactoRawValue.asunto, {
        validators: [Validators.required],
      }),
      mensaje: new FormControl(mensajeContactoRawValue.mensaje, {
        validators: [Validators.required],
      }),
      fechaEnvio: new FormControl(mensajeContactoRawValue.fechaEnvio, {
        validators: [Validators.required],
      }),
      atendido: new FormControl(mensajeContactoRawValue.atendido, {
        validators: [Validators.required],
      }),
    });
  }

  getMensajeContacto(form: MensajeContactoFormGroup): IMensajeContacto | NewMensajeContacto {
    return this.convertMensajeContactoRawValueToMensajeContacto(
      form.getRawValue() as MensajeContactoFormRawValue | NewMensajeContactoFormRawValue,
    );
  }

  resetForm(form: MensajeContactoFormGroup, mensajeContacto: MensajeContactoFormGroupInput): void {
    const mensajeContactoRawValue = this.convertMensajeContactoToMensajeContactoRawValue({ ...this.getFormDefaults(), ...mensajeContacto });
    form.reset(
      {
        ...mensajeContactoRawValue,
        id: { value: mensajeContactoRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): MensajeContactoFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      fechaEnvio: currentTime,
      atendido: false,
    };
  }

  private convertMensajeContactoRawValueToMensajeContacto(
    rawMensajeContacto: MensajeContactoFormRawValue | NewMensajeContactoFormRawValue,
  ): IMensajeContacto | NewMensajeContacto {
    return {
      ...rawMensajeContacto,
      fechaEnvio: dayjs(rawMensajeContacto.fechaEnvio, DATE_TIME_FORMAT),
    };
  }

  private convertMensajeContactoToMensajeContactoRawValue(
    mensajeContacto: IMensajeContacto | (Partial<NewMensajeContacto> & MensajeContactoFormDefaults),
  ): MensajeContactoFormRawValue | PartialWithRequiredKeyOf<NewMensajeContactoFormRawValue> {
    return {
      ...mensajeContacto,
      fechaEnvio: mensajeContacto.fechaEnvio ? mensajeContacto.fechaEnvio.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
