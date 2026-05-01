import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../solicitud-presupuesto.test-samples';

import { SolicitudPresupuestoFormService } from './solicitud-presupuesto-form.service';

describe('SolicitudPresupuesto Form Service', () => {
  let service: SolicitudPresupuestoFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SolicitudPresupuestoFormService);
  });

  describe('Service methods', () => {
    describe('createSolicitudPresupuestoFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSolicitudPresupuestoFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nombreCliente: expect.any(Object),
            email: expect.any(Object),
            telefono: expect.any(Object),
            nombreEmpresa: expect.any(Object),
            descripcion: expect.any(Object),
            cantidad: expect.any(Object),
            fechaSolicitud: expect.any(Object),
            estado: expect.any(Object),
            observacionesInternas: expect.any(Object),
            producto: expect.any(Object),
          }),
        );
      });

      it('passing ISolicitudPresupuesto should create a new form with FormGroup', () => {
        const formGroup = service.createSolicitudPresupuestoFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nombreCliente: expect.any(Object),
            email: expect.any(Object),
            telefono: expect.any(Object),
            nombreEmpresa: expect.any(Object),
            descripcion: expect.any(Object),
            cantidad: expect.any(Object),
            fechaSolicitud: expect.any(Object),
            estado: expect.any(Object),
            observacionesInternas: expect.any(Object),
            producto: expect.any(Object),
          }),
        );
      });
    });

    describe('getSolicitudPresupuesto', () => {
      it('should return NewSolicitudPresupuesto for default SolicitudPresupuesto initial value', () => {
        const formGroup = service.createSolicitudPresupuestoFormGroup(sampleWithNewData);

        const solicitudPresupuesto = service.getSolicitudPresupuesto(formGroup) as any;

        expect(solicitudPresupuesto).toMatchObject(sampleWithNewData);
      });

      it('should return NewSolicitudPresupuesto for empty SolicitudPresupuesto initial value', () => {
        const formGroup = service.createSolicitudPresupuestoFormGroup();

        const solicitudPresupuesto = service.getSolicitudPresupuesto(formGroup) as any;

        expect(solicitudPresupuesto).toMatchObject({});
      });

      it('should return ISolicitudPresupuesto', () => {
        const formGroup = service.createSolicitudPresupuestoFormGroup(sampleWithRequiredData);

        const solicitudPresupuesto = service.getSolicitudPresupuesto(formGroup) as any;

        expect(solicitudPresupuesto).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISolicitudPresupuesto should not enable id FormControl', () => {
        const formGroup = service.createSolicitudPresupuestoFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSolicitudPresupuesto should disable id FormControl', () => {
        const formGroup = service.createSolicitudPresupuestoFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
