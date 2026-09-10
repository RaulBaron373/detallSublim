import { HttpResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FontAwesomeTestingModule } from '@fortawesome/angular-fontawesome/testing';
import { ActivatedRoute, Router, convertToParamMap, provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';

import { AdminHistoriaService } from 'app/core/historias/admin-historia.service';
import { IHistoria } from 'app/core/historias/historia.model';

import { HistoriaFormComponent } from './historia-form.component';

interface AdminHistoriaServiceMock {
  find: jest.Mock;
  create: jest.Mock;
  update: jest.Mock;
  publish: jest.Mock;
  unpublish: jest.Mock;
  addImage: jest.Mock;
  getImage: jest.Mock;
  delete: jest.Mock;
  deleteImage: jest.Mock;
  setCover: jest.Mock;
  reorderImages: jest.Mock;
}

describe('HistoriaFormComponent', () => {
  let component: HistoriaFormComponent;
  let fixture: ComponentFixture<HistoriaFormComponent>;
  let service: AdminHistoriaServiceMock;
  let router: Router;

  const historia: IHistoria = {
    id: 7,
    titulo: 'Historia administrable',
    slug: 'historia-administrable',
    resumen: 'Resumen válido',
    contenido: 'Contenido válido de la historia.',
    estado: 'BORRADOR',
    fechaCreacion: '2026-09-10T10:00:00Z',
    fechaActualizacion: '2026-09-10T11:00:00Z',
    fechaPublicacion: null,
    imagenes: [
      {
        id: 10,
        textoAlternativo: 'Portada',
        orden: 0,
        portada: true,
        url: null,
      },
    ],
  };

  beforeEach(async () => {
    service = {
      find: jest.fn(),
      create: jest.fn(),
      update: jest.fn(),
      publish: jest.fn(),
      unpublish: jest.fn(),
      addImage: jest.fn(),
      getImage: jest.fn(),
      delete: jest.fn(),
      deleteImage: jest.fn(),
      setCover: jest.fn(),
      reorderImages: jest.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [HistoriaFormComponent, FontAwesomeTestingModule],
      providers: [
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
        {
          provide: AdminHistoriaService,
          useValue: service,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(HistoriaFormComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should allow publishing only when the draft is valid, saved and has exactly one cover', () => {
    component.historiaId = historia.id;
    component.historia = historia;
    component.form.setValue({
      titulo: historia.titulo,
      resumen: historia.resumen,
      contenido: historia.contenido,
    });
    component.form.markAsPristine();

    expect(component.canPublish).toBe(true);

    component.form.markAsDirty();

    expect(component.canPublish).toBe(false);
  });

  it('should open and cancel the delete confirmation', () => {
    component.historiaId = historia.id;
    component.historia = historia;

    component.requestDeleteHistoria();

    expect(component.deleteHistoriaRequested).toBe(true);

    component.cancelDeleteHistoria();

    expect(component.deleteHistoriaRequested).toBe(false);
    expect(component.deleteHistoriaError).toBe('');
  });

  it('should render the delete confirmation as a modal', () => {
    component.historiaId = historia.id;
    component.historia = historia;
    component.requestDeleteHistoria();

    fixture.detectChanges();

    const modal = fixture.nativeElement.querySelector('.historia-form__delete-modal') as HTMLElement | null;

    expect(modal).not.toBeNull();
    expect(modal?.textContent).toContain('Acción irreversible');
    expect(modal?.textContent).toContain(historia.titulo);
  });

  it('should delete the complete story and return to the admin list', () => {
    component.historiaId = historia.id;
    component.historia = historia;

    service.delete.mockReturnValue(of(new HttpResponse<void>({ status: 204 })));
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);

    component.requestDeleteHistoria();
    component.deleteHistoria();

    expect(service.delete).toHaveBeenCalledWith(historia.id);
    expect(navigateSpy).toHaveBeenCalledWith(['/panel/historias']);
    expect(component.deleteHistoriaRequested).toBe(false);
    expect(component.deleteHistoriaError).toBe('');
    expect(component.isDeletingHistoria).toBe(false);
  });

  it('should keep the confirmation open and expose an error when deletion fails', () => {
    component.historiaId = historia.id;
    component.historia = historia;
    component.deleteHistoriaRequested = true;
    service.delete.mockReturnValue(throwError(() => new Error('Delete failed')));

    component.deleteHistoria();

    expect(component.deleteHistoriaRequested).toBe(true);
    expect(component.deleteHistoriaError).toBe('No se pudo eliminar la historia. Inténtalo de nuevo.');
    expect(component.isDeletingHistoria).toBe(false);
  });
});
