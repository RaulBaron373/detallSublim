import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ISolicitudPresupuesto } from '../solicitud-presupuesto.model';
import {
  sampleWithFullData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithRequiredData,
} from '../solicitud-presupuesto.test-samples';

import { RestSolicitudPresupuesto, SolicitudPresupuestoService } from './solicitud-presupuesto.service';

const requireRestSample: RestSolicitudPresupuesto = {
  ...sampleWithRequiredData,
  fechaSolicitud: sampleWithRequiredData.fechaSolicitud?.toJSON(),
};

describe('SolicitudPresupuesto Service', () => {
  let service: SolicitudPresupuestoService;
  let httpMock: HttpTestingController;
  let expectedResult: ISolicitudPresupuesto | ISolicitudPresupuesto[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(SolicitudPresupuestoService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a SolicitudPresupuesto', () => {
      const solicitudPresupuesto = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(solicitudPresupuesto).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SolicitudPresupuesto', () => {
      const solicitudPresupuesto = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(solicitudPresupuesto).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SolicitudPresupuesto', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SolicitudPresupuesto', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a SolicitudPresupuesto', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addSolicitudPresupuestoToCollectionIfMissing', () => {
      it('should add a SolicitudPresupuesto to an empty array', () => {
        const solicitudPresupuesto: ISolicitudPresupuesto = sampleWithRequiredData;
        expectedResult = service.addSolicitudPresupuestoToCollectionIfMissing([], solicitudPresupuesto);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(solicitudPresupuesto);
      });

      it('should not add a SolicitudPresupuesto to an array that contains it', () => {
        const solicitudPresupuesto: ISolicitudPresupuesto = sampleWithRequiredData;
        const solicitudPresupuestoCollection: ISolicitudPresupuesto[] = [
          {
            ...solicitudPresupuesto,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSolicitudPresupuestoToCollectionIfMissing(solicitudPresupuestoCollection, solicitudPresupuesto);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SolicitudPresupuesto to an array that doesn't contain it", () => {
        const solicitudPresupuesto: ISolicitudPresupuesto = sampleWithRequiredData;
        const solicitudPresupuestoCollection: ISolicitudPresupuesto[] = [sampleWithPartialData];
        expectedResult = service.addSolicitudPresupuestoToCollectionIfMissing(solicitudPresupuestoCollection, solicitudPresupuesto);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(solicitudPresupuesto);
      });

      it('should add only unique SolicitudPresupuesto to an array', () => {
        const solicitudPresupuestoArray: ISolicitudPresupuesto[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const solicitudPresupuestoCollection: ISolicitudPresupuesto[] = [sampleWithRequiredData];
        expectedResult = service.addSolicitudPresupuestoToCollectionIfMissing(solicitudPresupuestoCollection, ...solicitudPresupuestoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const solicitudPresupuesto: ISolicitudPresupuesto = sampleWithRequiredData;
        const solicitudPresupuesto2: ISolicitudPresupuesto = sampleWithPartialData;
        expectedResult = service.addSolicitudPresupuestoToCollectionIfMissing([], solicitudPresupuesto, solicitudPresupuesto2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(solicitudPresupuesto);
        expect(expectedResult).toContain(solicitudPresupuesto2);
      });

      it('should accept null and undefined values', () => {
        const solicitudPresupuesto: ISolicitudPresupuesto = sampleWithRequiredData;
        expectedResult = service.addSolicitudPresupuestoToCollectionIfMissing([], null, solicitudPresupuesto, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(solicitudPresupuesto);
      });

      it('should return initial array if no SolicitudPresupuesto is added', () => {
        const solicitudPresupuestoCollection: ISolicitudPresupuesto[] = [sampleWithRequiredData];
        expectedResult = service.addSolicitudPresupuestoToCollectionIfMissing(solicitudPresupuestoCollection, undefined, null);
        expect(expectedResult).toEqual(solicitudPresupuestoCollection);
      });
    });

    describe('compareSolicitudPresupuesto', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSolicitudPresupuesto(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 18171 };
        const entity2 = null;

        const compareResult1 = service.compareSolicitudPresupuesto(entity1, entity2);
        const compareResult2 = service.compareSolicitudPresupuesto(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 18171 };
        const entity2 = { id: 19114 };

        const compareResult1 = service.compareSolicitudPresupuesto(entity1, entity2);
        const compareResult2 = service.compareSolicitudPresupuesto(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 18171 };
        const entity2 = { id: 18171 };

        const compareResult1 = service.compareSolicitudPresupuesto(entity1, entity2);
        const compareResult2 = service.compareSolicitudPresupuesto(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
