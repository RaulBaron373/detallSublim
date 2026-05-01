import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IMensajeContacto } from '../mensaje-contacto.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../mensaje-contacto.test-samples';

import { MensajeContactoService, RestMensajeContacto } from './mensaje-contacto.service';

const requireRestSample: RestMensajeContacto = {
  ...sampleWithRequiredData,
  fechaEnvio: sampleWithRequiredData.fechaEnvio?.toJSON(),
};

describe('MensajeContacto Service', () => {
  let service: MensajeContactoService;
  let httpMock: HttpTestingController;
  let expectedResult: IMensajeContacto | IMensajeContacto[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(MensajeContactoService);
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

    it('should create a MensajeContacto', () => {
      const mensajeContacto = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(mensajeContacto).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a MensajeContacto', () => {
      const mensajeContacto = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(mensajeContacto).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a MensajeContacto', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of MensajeContacto', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a MensajeContacto', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addMensajeContactoToCollectionIfMissing', () => {
      it('should add a MensajeContacto to an empty array', () => {
        const mensajeContacto: IMensajeContacto = sampleWithRequiredData;
        expectedResult = service.addMensajeContactoToCollectionIfMissing([], mensajeContacto);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(mensajeContacto);
      });

      it('should not add a MensajeContacto to an array that contains it', () => {
        const mensajeContacto: IMensajeContacto = sampleWithRequiredData;
        const mensajeContactoCollection: IMensajeContacto[] = [
          {
            ...mensajeContacto,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addMensajeContactoToCollectionIfMissing(mensajeContactoCollection, mensajeContacto);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a MensajeContacto to an array that doesn't contain it", () => {
        const mensajeContacto: IMensajeContacto = sampleWithRequiredData;
        const mensajeContactoCollection: IMensajeContacto[] = [sampleWithPartialData];
        expectedResult = service.addMensajeContactoToCollectionIfMissing(mensajeContactoCollection, mensajeContacto);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(mensajeContacto);
      });

      it('should add only unique MensajeContacto to an array', () => {
        const mensajeContactoArray: IMensajeContacto[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const mensajeContactoCollection: IMensajeContacto[] = [sampleWithRequiredData];
        expectedResult = service.addMensajeContactoToCollectionIfMissing(mensajeContactoCollection, ...mensajeContactoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const mensajeContacto: IMensajeContacto = sampleWithRequiredData;
        const mensajeContacto2: IMensajeContacto = sampleWithPartialData;
        expectedResult = service.addMensajeContactoToCollectionIfMissing([], mensajeContacto, mensajeContacto2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(mensajeContacto);
        expect(expectedResult).toContain(mensajeContacto2);
      });

      it('should accept null and undefined values', () => {
        const mensajeContacto: IMensajeContacto = sampleWithRequiredData;
        expectedResult = service.addMensajeContactoToCollectionIfMissing([], null, mensajeContacto, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(mensajeContacto);
      });

      it('should return initial array if no MensajeContacto is added', () => {
        const mensajeContactoCollection: IMensajeContacto[] = [sampleWithRequiredData];
        expectedResult = service.addMensajeContactoToCollectionIfMissing(mensajeContactoCollection, undefined, null);
        expect(expectedResult).toEqual(mensajeContactoCollection);
      });
    });

    describe('compareMensajeContacto', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareMensajeContacto(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 12541 };
        const entity2 = null;

        const compareResult1 = service.compareMensajeContacto(entity1, entity2);
        const compareResult2 = service.compareMensajeContacto(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 12541 };
        const entity2 = { id: 26109 };

        const compareResult1 = service.compareMensajeContacto(entity1, entity2);
        const compareResult2 = service.compareMensajeContacto(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 12541 };
        const entity2 = { id: 12541 };

        const compareResult1 = service.compareMensajeContacto(entity1, entity2);
        const compareResult2 = service.compareMensajeContacto(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
