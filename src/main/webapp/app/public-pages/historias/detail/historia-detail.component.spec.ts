import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';

import { IHistoria } from 'app/core/historias/historia.model';
import { PublicHistoriaService } from 'app/core/historias/public-historia.service';

import { HistoriaDetailComponent } from './historia-detail.component';

describe('HistoriaDetailComponent', () => {
  let component: HistoriaDetailComponent;
  let fixture: ComponentFixture<HistoriaDetailComponent>;
  let service: jest.Mocked<PublicHistoriaService>;

  const historia: IHistoria = {
    id: 1,
    titulo: 'Historia de ejemplo',
    slug: 'historia-de-ejemplo',
    resumen: 'Resumen de ejemplo',
    contenido: 'Contenido completo de la historia.',
    estado: 'PUBLICADA',
    fechaCreacion: '2026-09-09T09:00:00Z',
    fechaActualizacion: '2026-09-09T10:00:00Z',
    fechaPublicacion: '2026-09-09T10:00:00Z',
    imagenes: [
      {
        id: 3,
        textoAlternativo: 'Imagen tercera',
        orden: 3,
        portada: false,
        url: '/api/public/historias/imagenes/3',
      },
      {
        id: 1,
        textoAlternativo: 'Portada',
        orden: 1,
        portada: true,
        url: '/api/public/historias/imagenes/1',
      },
      {
        id: 2,
        textoAlternativo: 'Imagen segunda',
        orden: 2,
        portada: false,
        url: '/api/public/historias/imagenes/2',
      },
    ],
  };

  beforeEach(async () => {
    service = {
      query: jest.fn(),
      findBySlug: jest.fn(),
    } as unknown as jest.Mocked<PublicHistoriaService>;

    service.findBySlug.mockReturnValue(of(historia));

    await TestBed.configureTestingModule({
      imports: [HistoriaDetailComponent],
      providers: [
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({
                slug: 'historia-de-ejemplo',
              }),
            },
          },
        },
        {
          provide: PublicHistoriaService,
          useValue: service,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(HistoriaDetailComponent);
    component = fixture.componentInstance;
  });

  it('should create and load the story from the route slug', () => {
    fixture.detectChanges();

    expect(component).toBeTruthy();
    expect(service.findBySlug).toHaveBeenCalledWith('historia-de-ejemplo');
    expect(component.historia).toEqual(historia);
    expect(component.hasError).toBe(false);
    expect(component.notFound).toBe(false);
    expect(component.isLoading).toBe(false);
  });

  it('should expose the cover image', () => {
    fixture.detectChanges();

    expect(component.portada?.id).toBe(1);
    expect(component.portada?.portada).toBe(true);
  });

  it('should return secondary images without the cover and ordered by orden', () => {
    fixture.detectChanges();

    expect(component.imagenesSecundarias.map(imagen => imagen.id)).toEqual([2, 3]);
  });

  it('should distribute secondary images between story paragraphs', () => {
    service.findBySlug.mockReturnValue(
      of({
        ...historia,
        contenido: 'Primer párrafo.\n\nSegundo párrafo.\n\nTercer párrafo.',
      }),
    );

    fixture.detectChanges();

    expect(component.storySections.map(section => section.paragraph)).toEqual(['Primer párrafo.', 'Segundo párrafo.', 'Tercer párrafo.']);
    expect(component.storySections.flatMap(section => section.images).map(media => media.image.id)).toEqual([2, 3]);
    expect(component.storySections[0]?.images[0]?.layout).toBe('offset-right');
    expect(component.storySections[1]?.images[0]?.layout).toBe('offset-left');
  });

  it('should render secondary images inside the story instead of a separate gallery', () => {
    fixture.detectChanges();

    const element = fixture.nativeElement as HTMLElement;

    expect(element.querySelector('.historia-detail-gallery')).toBeNull();
    expect(element.querySelectorAll('.historia-detail-story-media')).toHaveLength(2);
  });

  it('should use the first ordered image as fallback when there is no explicit cover', () => {
    service.findBySlug.mockReturnValue(
      of({
        ...historia,
        imagenes: historia.imagenes.map(imagen => ({
          ...imagen,
          portada: false,
        })),
      }),
    );

    fixture.detectChanges();

    expect(component.portada?.id).toBe(1);
  });

  it('should handle a 404 response as not found', () => {
    service.findBySlug.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 404,
          }),
      ),
    );

    fixture.detectChanges();

    expect(component.historia).toBeNull();
    expect(component.notFound).toBe(true);
    expect(component.hasError).toBe(false);
    expect(component.isLoading).toBe(false);
  });

  it('should handle a technical error', () => {
    service.findBySlug.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 500,
          }),
      ),
    );

    fixture.detectChanges();

    expect(component.historia).toBeNull();
    expect(component.notFound).toBe(false);
    expect(component.hasError).toBe(true);
    expect(component.isLoading).toBe(false);
  });
});
