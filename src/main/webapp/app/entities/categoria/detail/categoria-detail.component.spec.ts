import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { DataUtils } from 'app/core/util/data-util.service';

import { CategoriaDetailComponent } from './categoria-detail.component';

describe('Categoria Management Detail Component', () => {
  let comp: CategoriaDetailComponent;
  let fixture: ComponentFixture<CategoriaDetailComponent>;
  let dataUtils: DataUtils;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CategoriaDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./categoria-detail.component').then(m => m.CategoriaDetailComponent),
              resolve: { categoria: () => of({ id: 24962 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(CategoriaDetailComponent, '')
      .compileComponents();
    dataUtils = TestBed.inject(DataUtils);
    jest.spyOn(window, 'open').mockImplementation(() => null);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CategoriaDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load categoria on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', CategoriaDetailComponent);

      // THEN
      expect(instance.categoria()).toEqual(expect.objectContaining({ id: 24962 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });

  describe('byteSize', () => {
    it('should call byteSize from DataUtils', () => {
      // GIVEN
      jest.spyOn(dataUtils, 'byteSize');
      const fakeBase64 = 'fake base64';

      // WHEN
      comp.byteSize(fakeBase64);

      // THEN
      expect(dataUtils.byteSize).toHaveBeenCalledWith(fakeBase64);
    });
  });

  describe('openFile', () => {
    it('should call openFile from DataUtils', () => {
      const newWindow = { ...window };
      window.open = jest.fn(() => newWindow);
      window.onload = jest.fn(() => newWindow) as any;
      window.URL.createObjectURL = jest.fn() as any;
      // GIVEN
      jest.spyOn(dataUtils, 'openFile');
      const fakeContentType = 'fake content type';
      const fakeBase64 = 'fake base64';

      // WHEN
      comp.openFile(fakeBase64, fakeContentType);

      // THEN
      expect(dataUtils.openFile).toHaveBeenCalledWith(fakeBase64, fakeContentType);
    });
  });
});
