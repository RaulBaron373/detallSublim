import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IHistoria, IHistoriaEditable, IHistoriaImagen } from './historia.model';
import { AdminHistoriaService } from './admin-historia.service';

describe('AdminHistoriaService', () => {
  let service: AdminHistoriaService;
  let httpMock: HttpTestingController;

  const historia: IHistoria = {
    id: 1,
    titulo: 'Historia de ejemplo',
    slug: 'historia-de-ejemplo',
    resumen: 'Resumen de ejemplo',
    contenido: 'Contenido completo.',
    estado: 'BORRADOR',
    fechaCreacion: '2026-09-09T10:00:00Z',
    fechaActualizacion: '2026-09-09T10:00:00Z',
    fechaPublicacion: null,
    imagenes: [],
  };

  const editable: IHistoriaEditable = {
    titulo: 'Historia de ejemplo',
    resumen: 'Resumen de ejemplo',
    contenido: 'Contenido completo.',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });

    service = TestBed.inject(AdminHistoriaService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should query stories with pagination', () => {
    service.query({ page: 0, size: 10 }).subscribe(response => {
      expect(response.body).toEqual([historia]);
    });

    const req = httpMock.expectOne(request => request.method === 'GET' && request.url === 'api/admin/historias');

    expect(req.request.params.get('page')).toBe('0');
    expect(req.request.params.get('size')).toBe('10');

    req.flush([historia]);
  });

  it('should find a story by id', () => {
    service.find(1).subscribe(response => {
      expect(response.body).toEqual(historia);
    });

    const req = httpMock.expectOne('api/admin/historias/1');

    expect(req.request.method).toBe('GET');

    req.flush(historia);
  });

  it('should create a story', () => {
    service.create(editable).subscribe(response => {
      expect(response.body).toEqual(historia);
    });

    const req = httpMock.expectOne('api/admin/historias');

    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(editable);

    req.flush(historia);
  });

  it('should update a story', () => {
    service.update(1, editable).subscribe(response => {
      expect(response.body).toEqual(historia);
    });

    const req = httpMock.expectOne('api/admin/historias/1');

    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(editable);

    req.flush(historia);
  });

  it('should publish a story', () => {
    service.publish(1).subscribe();

    const req = httpMock.expectOne('api/admin/historias/1/publicar');

    expect(req.request.method).toBe('POST');
    expect(req.request.body).toBeNull();

    req.flush({
      ...historia,
      estado: 'PUBLICADA',
    });
  });

  it('should unpublish a story', () => {
    service.unpublish(1).subscribe();

    const req = httpMock.expectOne('api/admin/historias/1/despublicar');

    expect(req.request.method).toBe('POST');
    expect(req.request.body).toBeNull();

    req.flush(historia);
  });

  it('should upload an image as multipart form data', () => {
    const file = new File(['image'], 'foto.jpg', {
      type: 'image/jpeg',
    });

    const imagen: IHistoriaImagen = {
      id: 5,
      textoAlternativo: 'Imagen principal',
      orden: 0,
      portada: true,
      url: '/api/public/historias/imagenes/5',
    };

    service.addImage(1, file, 'Imagen principal', true).subscribe(response => {
      expect(response.body).toEqual(imagen);
    });

    const req = httpMock.expectOne('api/admin/historias/1/imagenes');

    expect(req.request.method).toBe('POST');
    expect(req.request.body).toBeInstanceOf(FormData);

    const formData = req.request.body as FormData;

    expect(formData.get('file')).toBe(file);
    expect(formData.get('textoAlternativo')).toBe('Imagen principal');
    expect(formData.get('portada')).toBe('true');

    req.flush(imagen);
  });

  it('should delete a story', () => {
    service.delete(1).subscribe();

    const req = httpMock.expectOne('api/admin/historias/1');

    expect(req.request.method).toBe('DELETE');

    req.flush(null);
  });

  it('should delete an image', () => {
    service.deleteImage(1, 5).subscribe();

    const req = httpMock.expectOne('api/admin/historias/1/imagenes/5');

    expect(req.request.method).toBe('DELETE');

    req.flush(null);
  });

  it('should set an image as cover', () => {
    service.setCover(1, 5).subscribe();

    const req = httpMock.expectOne('api/admin/historias/1/imagenes/5/portada');

    expect(req.request.method).toBe('POST');
    expect(req.request.body).toBeNull();

    req.flush({
      id: 5,
      textoAlternativo: 'Portada',
      orden: 0,
      portada: true,
      url: '/api/public/historias/imagenes/5',
    });
  });

  it('should reorder images', () => {
    service.reorderImages(1, [5, 3, 8]).subscribe();

    const req = httpMock.expectOne('api/admin/historias/1/imagenes/orden');

    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual([5, 3, 8]);

    req.flush(null);
  });

  it('should download an admin historia image as blob', () => {
    const historiaId = 12;
    const imagenId = 34;
    const blob = new Blob(['image-data'], { type: 'image/png' });

    service.getImage(historiaId, imagenId).subscribe(response => {
      expect(response.body).toEqual(blob);
    });

    const req = httpMock.expectOne(`api/admin/historias/${historiaId}/imagenes/${imagenId}`);

    expect(req.request.method).toBe('GET');
    expect(req.request.responseType).toBe('blob');

    req.flush(blob);
  });
});
