import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../mensaje-contacto.test-samples';

import { MensajeContactoFormService } from './mensaje-contacto-form.service';

describe('MensajeContacto Form Service', () => {
  let service: MensajeContactoFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MensajeContactoFormService);
  });

  describe('Service methods', () => {
    describe('createMensajeContactoFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createMensajeContactoFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nombre: expect.any(Object),
            email: expect.any(Object),
            telefono: expect.any(Object),
            asunto: expect.any(Object),
            mensaje: expect.any(Object),
            fechaEnvio: expect.any(Object),
            atendido: expect.any(Object),
          }),
        );
      });

      it('passing IMensajeContacto should create a new form with FormGroup', () => {
        const formGroup = service.createMensajeContactoFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nombre: expect.any(Object),
            email: expect.any(Object),
            telefono: expect.any(Object),
            asunto: expect.any(Object),
            mensaje: expect.any(Object),
            fechaEnvio: expect.any(Object),
            atendido: expect.any(Object),
          }),
        );
      });
    });

    describe('getMensajeContacto', () => {
      it('should return NewMensajeContacto for default MensajeContacto initial value', () => {
        const formGroup = service.createMensajeContactoFormGroup(sampleWithNewData);

        const mensajeContacto = service.getMensajeContacto(formGroup) as any;

        expect(mensajeContacto).toMatchObject(sampleWithNewData);
      });

      it('should return NewMensajeContacto for empty MensajeContacto initial value', () => {
        const formGroup = service.createMensajeContactoFormGroup();

        const mensajeContacto = service.getMensajeContacto(formGroup) as any;

        expect(mensajeContacto).toMatchObject({});
      });

      it('should return IMensajeContacto', () => {
        const formGroup = service.createMensajeContactoFormGroup(sampleWithRequiredData);

        const mensajeContacto = service.getMensajeContacto(formGroup) as any;

        expect(mensajeContacto).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IMensajeContacto should not enable id FormControl', () => {
        const formGroup = service.createMensajeContactoFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewMensajeContacto should disable id FormControl', () => {
        const formGroup = service.createMensajeContactoFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
