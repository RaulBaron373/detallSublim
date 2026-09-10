import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IHistoria, IHistoriaResumen } from './historia.model';
import { PublicHistoriaService } from './public-historia.service';

describe('PublicHistoriaService', () => {
  let service: PublicHistoriaService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });

    service = TestBed.inject(PublicHistoriaService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should query published stories with pagination parameters', () => {
    const historia: IHistoriaResumen = {
      id: 1,
      titulo: 'Historia de ejemplo',
      slug: 'historia-de-ejemplo',
      resumen: 'Resumen de la historia',
      fechaPublicacion: '2026-09-09T10:00:00Z',
      portadaId: 5,
      portadaTextoAlternativo: 'Portada de ejemplo',
      portadaUrl: '/api/public/historias/imagenes/5',
    };

    service
      .query({
        page: 0,
        size: 9,
        sort: 'fechaPublicacion,desc',
      })
      .subscribe(response => {
        expect(response.body).toEqual([historia]);
        expect(response.headers.get('X-Total-Count')).toBe('1');
      });

    const req = httpMock.expectOne(request => request.method === 'GET' && request.url === 'api/public/historias');

    expect(req.request.params.get('page')).toBe('0');
    expect(req.request.params.get('size')).toBe('9');
    expect(req.request.params.get('sort')).toBe('fechaPublicacion,desc');

    req.flush([historia], {
      headers: {
        'X-Total-Count': '1',
      },
    });
  });

  it('should find a published story by slug', () => {
    const historia: IHistoria = {
      id: 1,
      titulo: 'Historia de ejemplo',
      slug: 'historia-de-ejemplo',
      resumen: 'Resumen de la historia',
      contenido: 'Contenido completo de la historia.',
      estado: 'PUBLICADA',
      fechaCreacion: '2026-09-09T09:00:00Z',
      fechaActualizacion: '2026-09-09T10:00:00Z',
      fechaPublicacion: '2026-09-09T10:00:00Z',
      imagenes: [],
    };

    service.findBySlug('historia-de-ejemplo').subscribe(response => {
      expect(response).toEqual(historia);
    });

    const req = httpMock.expectOne('api/public/historias/historia-de-ejemplo');

    expect(req.request.method).toBe('GET');

    req.flush(historia);
  });

  it('should encode the slug before sending the request', () => {
    service.findBySlug('historia especial/2026').subscribe();

    const req = httpMock.expectOne('api/public/historias/historia%20especial%2F2026');

    expect(req.request.method).toBe('GET');

    req.flush({
      id: 1,
      titulo: 'Historia',
      slug: 'historia-especial-2026',
      resumen: 'Resumen',
      contenido: 'Contenido',
      estado: 'PUBLICADA',
      fechaCreacion: null,
      fechaActualizacion: null,
      fechaPublicacion: null,
      imagenes: [],
    });
  });
});
