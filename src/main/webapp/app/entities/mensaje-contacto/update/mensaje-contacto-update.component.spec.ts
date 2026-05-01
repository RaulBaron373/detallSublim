import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { MensajeContactoService } from '../service/mensaje-contacto.service';
import { IMensajeContacto } from '../mensaje-contacto.model';
import { MensajeContactoFormService } from './mensaje-contacto-form.service';

import { MensajeContactoUpdateComponent } from './mensaje-contacto-update.component';

describe('MensajeContacto Management Update Component', () => {
  let comp: MensajeContactoUpdateComponent;
  let fixture: ComponentFixture<MensajeContactoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let mensajeContactoFormService: MensajeContactoFormService;
  let mensajeContactoService: MensajeContactoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MensajeContactoUpdateComponent],
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
      .overrideTemplate(MensajeContactoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MensajeContactoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    mensajeContactoFormService = TestBed.inject(MensajeContactoFormService);
    mensajeContactoService = TestBed.inject(MensajeContactoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const mensajeContacto: IMensajeContacto = { id: 26109 };

      activatedRoute.data = of({ mensajeContacto });
      comp.ngOnInit();

      expect(comp.mensajeContacto).toEqual(mensajeContacto);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMensajeContacto>>();
      const mensajeContacto = { id: 12541 };
      jest.spyOn(mensajeContactoFormService, 'getMensajeContacto').mockReturnValue(mensajeContacto);
      jest.spyOn(mensajeContactoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ mensajeContacto });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: mensajeContacto }));
      saveSubject.complete();

      // THEN
      expect(mensajeContactoFormService.getMensajeContacto).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(mensajeContactoService.update).toHaveBeenCalledWith(expect.objectContaining(mensajeContacto));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMensajeContacto>>();
      const mensajeContacto = { id: 12541 };
      jest.spyOn(mensajeContactoFormService, 'getMensajeContacto').mockReturnValue({ id: null });
      jest.spyOn(mensajeContactoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ mensajeContacto: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: mensajeContacto }));
      saveSubject.complete();

      // THEN
      expect(mensajeContactoFormService.getMensajeContacto).toHaveBeenCalled();
      expect(mensajeContactoService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMensajeContacto>>();
      const mensajeContacto = { id: 12541 };
      jest.spyOn(mensajeContactoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ mensajeContacto });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(mensajeContactoService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
