import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FontAwesomeTestingModule } from '@fortawesome/angular-fontawesome/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';

import { AdminHistoriaService } from 'app/core/historias/admin-historia.service';
import { IHistoria } from 'app/core/historias/historia.model';

import { HistoriasAdminComponent } from './historias-admin.component';

describe('HistoriasAdminComponent', () => {
  let component: HistoriasAdminComponent;
  let fixture: ComponentFixture<HistoriasAdminComponent>;
  let service: jest.Mocked<AdminHistoriaService>;

  const historia: IHistoria = {
    id: 1,
    titulo: 'Historia de ejemplo',
    slug: 'historia-de-ejemplo',
    resumen: 'Resumen de ejemplo',
    contenido: 'Contenido de ejemplo',
    estado: 'BORRADOR',
    fechaCreacion: '2026-09-09T10:00:00Z',
    fechaActualizacion: '2026-09-09T11:00:00Z',
    fechaPublicacion: null,
    imagenes: [],
  };

  beforeEach(async () => {
    const adminHistoriaServiceMock = {
      query: jest.fn(),
      publish: jest.fn(),
      unpublish: jest.fn(),
    };

    adminHistoriaServiceMock.query.mockReturnValue(
      of(
        new HttpResponse<IHistoria[]>({
          body: [],
          headers: new HttpHeaders({
            'X-Total-Count': '0',
          }),
        }),
      ),
    );

    await TestBed.configureTestingModule({
      imports: [HistoriasAdminComponent, FontAwesomeTestingModule],
      providers: [
        provideRouter([]),
        {
          provide: AdminHistoriaService,
          useValue: adminHistoriaServiceMock,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(HistoriasAdminComponent);
    component = fixture.componentInstance;
    service = TestBed.inject(AdminHistoriaService) as jest.Mocked<AdminHistoriaService>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load the first page on init', () => {
    service.query.mockReturnValue(
      of(
        new HttpResponse<IHistoria[]>({
          body: [historia],
          headers: new HttpHeaders({
            'X-Total-Count': '1',
          }),
        }),
      ),
    );

    fixture.detectChanges();

    expect(service.query).toHaveBeenCalledWith({
      page: 0,
      size: 10,
      sort: 'fechaActualizacion,desc',
    });

    expect(component.historias).toEqual([historia]);
    expect(component.totalItems).toBe(1);
    expect(component.hasError).toBe(false);
    expect(component.isLoading).toBe(false);
  });

  it('should convert visual page to backend zero-based page', () => {
    component.loadPage(3);

    expect(service.query).toHaveBeenCalledWith({
      page: 2,
      size: 10,
      sort: 'fechaActualizacion,desc',
    });
  });

  it('should use story count when total header is invalid', () => {
    service.query.mockReturnValue(
      of(
        new HttpResponse<IHistoria[]>({
          body: [historia],
          headers: new HttpHeaders({
            'X-Total-Count': 'invalid',
          }),
        }),
      ),
    );

    component.loadPage();

    expect(component.totalItems).toBe(1);
  });

  it('should set error state when loading fails', () => {
    component.historias = [historia];
    component.totalItems = 1;

    service.query.mockReturnValue(throwError(() => new Error('Request failed')));

    component.loadPage();

    expect(component.historias).toEqual([]);
    expect(component.totalItems).toBe(0);
    expect(component.hasError).toBe(true);
    expect(component.isLoading).toBe(false);
  });

  it('should publish a draft directly from the list', () => {
    const publishedHistoria: IHistoria = {
      ...historia,
      estado: 'PUBLICADA',
      fechaPublicacion: '2026-09-10T12:00:00Z',
    };

    service.publish.mockReturnValue(of(new HttpResponse<IHistoria>({ body: publishedHistoria })));
    component.historias = [historia];

    component.togglePublication(historia);

    expect(service.publish).toHaveBeenCalledWith(historia.id);
    expect(service.unpublish).not.toHaveBeenCalled();
    expect(component.historias).toEqual([publishedHistoria]);
    expect(component.changingStateId).toBeNull();
    expect(component.stateChangeError).toBe('');
  });

  it('should move a published story back to draft directly from the list', () => {
    const publishedHistoria: IHistoria = {
      ...historia,
      estado: 'PUBLICADA',
      fechaPublicacion: '2026-09-10T12:00:00Z',
    };
    const draftHistoria: IHistoria = {
      ...publishedHistoria,
      estado: 'BORRADOR',
    };

    service.unpublish.mockReturnValue(of(new HttpResponse<IHistoria>({ body: draftHistoria })));
    component.historias = [publishedHistoria];

    component.togglePublication(publishedHistoria);

    expect(service.unpublish).toHaveBeenCalledWith(historia.id);
    expect(service.publish).not.toHaveBeenCalled();
    expect(component.historias).toEqual([draftHistoria]);
    expect(component.changingStateId).toBeNull();
  });

  it('should explain why quick publication failed', () => {
    service.publish.mockReturnValue(throwError(() => new Error('Publication rejected')));
    component.historias = [historia];

    component.togglePublication(historia);

    expect(component.stateChangeError).toContain('No se pudo publicar');
    expect(component.stateChangeError).toContain('exactamente una portada');
    expect(component.changingStateId).toBeNull();
  });
});
