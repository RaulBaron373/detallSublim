import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';

import { IHistoriaResumen } from 'app/core/historias/historia.model';
import { PublicHistoriaService } from 'app/core/historias/public-historia.service';

import { HistoriasComponent } from './historias.component';

describe('HistoriasComponent', () => {
  let component: HistoriasComponent;
  let fixture: ComponentFixture<HistoriasComponent>;
  let service: jest.Mocked<PublicHistoriaService>;

  const historia: IHistoriaResumen = {
    id: 1,
    titulo: 'Historia de ejemplo',
    slug: 'historia-de-ejemplo',
    resumen: 'Resumen de ejemplo',
    fechaPublicacion: '2026-09-09T10:00:00Z',
    portadaId: 5,
    portadaTextoAlternativo: 'Portada',
    portadaUrl: '/api/public/historias/imagenes/5',
  };

  beforeEach(async () => {
    service = {
      query: jest.fn(),
      findBySlug: jest.fn(),
    } as unknown as jest.Mocked<PublicHistoriaService>;

    service.query.mockReturnValue(
      of(
        new HttpResponse({
          body: [historia],
          headers: new HttpHeaders({
            'X-Total-Count': '1',
          }),
        }),
      ),
    );

    await TestBed.configureTestingModule({
      imports: [HistoriasComponent],
      providers: [
        provideRouter([]),
        {
          provide: PublicHistoriaService,
          useValue: service,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(HistoriasComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.detectChanges();

    expect(component).toBeTruthy();
  });

  it('should load the first page on init', () => {
    fixture.detectChanges();

    expect(service.query).toHaveBeenCalledWith({
      page: 0,
      size: 9,
      sort: 'fechaPublicacion,desc',
    });

    expect(component.historias).toEqual([historia]);
    expect(component.totalItems).toBe(1);
    expect(component.hasError).toBe(false);
    expect(component.isLoading).toBe(false);
  });

  it('should convert the visual page number to zero-based backend pagination', () => {
    fixture.detectChanges();

    component.loadPage(3);

    expect(service.query).toHaveBeenLastCalledWith({
      page: 2,
      size: 9,
      sort: 'fechaPublicacion,desc',
    });

    expect(component.page).toBe(3);
  });

  it('should handle loading errors', () => {
    service.query.mockReturnValueOnce(throwError(() => new Error('Network error')));

    fixture.detectChanges();

    expect(component.historias).toEqual([]);
    expect(component.totalItems).toBe(0);
    expect(component.hasError).toBe(true);
    expect(component.isLoading).toBe(false);
  });
});
