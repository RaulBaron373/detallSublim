import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IProducto } from 'app/entities/producto/producto.model';
import { ProductoService } from 'app/entities/producto/service/producto.service';
import { SolicitudPresupuestoService } from '../service/solicitud-presupuesto.service';
import { ISolicitudPresupuesto } from '../solicitud-presupuesto.model';
import { SolicitudPresupuestoFormService } from './solicitud-presupuesto-form.service';

import { SolicitudPresupuestoUpdateComponent } from './solicitud-presupuesto-update.component';

describe('SolicitudPresupuesto Management Update Component', () => {
  let comp: SolicitudPresupuestoUpdateComponent;
  let fixture: ComponentFixture<SolicitudPresupuestoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let solicitudPresupuestoFormService: SolicitudPresupuestoFormService;
  let solicitudPresupuestoService: SolicitudPresupuestoService;
  let productoService: ProductoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [SolicitudPresupuestoUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(SolicitudPresupuestoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SolicitudPresupuestoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    solicitudPresupuestoFormService = TestBed.inject(SolicitudPresupuestoFormService);
    solicitudPresupuestoService = TestBed.inject(SolicitudPresupuestoService);
    productoService = TestBed.inject(ProductoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Producto query and add missing value', () => {
      const solicitudPresupuesto: ISolicitudPresupuesto = { id: 19114 };
      const producto: IProducto = { id: 1896 };
      solicitudPresupuesto.producto = producto;

      const productoCollection: IProducto[] = [{ id: 1896 }];
      jest.spyOn(productoService, 'query').mockReturnValue(of(new HttpResponse({ body: productoCollection })));
      const additionalProductos = [producto];
      const expectedCollection: IProducto[] = [...additionalProductos, ...productoCollection];
      jest.spyOn(productoService, 'addProductoToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ solicitudPresupuesto });
      comp.ngOnInit();

      expect(productoService.query).toHaveBeenCalled();
      expect(productoService.addProductoToCollectionIfMissing).toHaveBeenCalledWith(
        productoCollection,
        ...additionalProductos.map(expect.objectContaining),
      );
      expect(comp.productosSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const solicitudPresupuesto: ISolicitudPresupuesto = { id: 19114 };
      const producto: IProducto = { id: 1896 };
      solicitudPresupuesto.producto = producto;

      activatedRoute.data = of({ solicitudPresupuesto });
      comp.ngOnInit();

      expect(comp.productosSharedCollection).toContainEqual(producto);
      expect(comp.solicitudPresupuesto).toEqual(solicitudPresupuesto);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISolicitudPresupuesto>>();
      const solicitudPresupuesto = { id: 18171 };
      jest.spyOn(solicitudPresupuestoFormService, 'getSolicitudPresupuesto').mockReturnValue(solicitudPresupuesto);
      jest.spyOn(solicitudPresupuestoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ solicitudPresupuesto });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: solicitudPresupuesto }));
      saveSubject.complete();

      // THEN
      expect(solicitudPresupuestoFormService.getSolicitudPresupuesto).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(solicitudPresupuestoService.update).toHaveBeenCalledWith(expect.objectContaining(solicitudPresupuesto));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISolicitudPresupuesto>>();
      const solicitudPresupuesto = { id: 18171 };
      jest.spyOn(solicitudPresupuestoFormService, 'getSolicitudPresupuesto').mockReturnValue({ id: null });
      jest.spyOn(solicitudPresupuestoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ solicitudPresupuesto: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: solicitudPresupuesto }));
      saveSubject.complete();

      // THEN
      expect(solicitudPresupuestoFormService.getSolicitudPresupuesto).toHaveBeenCalled();
      expect(solicitudPresupuestoService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISolicitudPresupuesto>>();
      const solicitudPresupuesto = { id: 18171 };
      jest.spyOn(solicitudPresupuestoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ solicitudPresupuesto });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(solicitudPresupuestoService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareProducto', () => {
      it('should forward to productoService', () => {
        const entity = { id: 1896 };
        const entity2 = { id: 15581 };
        jest.spyOn(productoService, 'compareProducto');
        comp.compareProducto(entity, entity2);
        expect(productoService.compareProducto).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
