import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';

import { IHistoria, IHistoriaEditable, IHistoriaImagen } from './historia.model';

export type HistoriaResponseType = HttpResponse<IHistoria>;
export type HistoriaArrayResponseType = HttpResponse<IHistoria[]>;
export type HistoriaImagenResponseType = HttpResponse<IHistoriaImagen>;
export type HistoriaImageBlobResponseType = HttpResponse<Blob>;

@Injectable({
  providedIn: 'root',
})
export class AdminHistoriaService {
  private readonly http = inject(HttpClient);
  private readonly applicationConfigService = inject(ApplicationConfigService);

  private readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/admin/historias');

  query(req?: { page?: number; size?: number; sort?: string | string[] }): Observable<HistoriaArrayResponseType> {
    const options = createRequestOption(req);

    return this.http.get<IHistoria[]>(this.resourceUrl, {
      params: options,
      observe: 'response',
    });
  }

  find(id: number): Observable<HistoriaResponseType> {
    return this.http.get<IHistoria>(`${this.resourceUrl}/${id}`, {
      observe: 'response',
    });
  }

  create(historia: IHistoriaEditable): Observable<HistoriaResponseType> {
    return this.http.post<IHistoria>(this.resourceUrl, historia, {
      observe: 'response',
    });
  }

  update(id: number, historia: IHistoriaEditable): Observable<HistoriaResponseType> {
    return this.http.put<IHistoria>(`${this.resourceUrl}/${id}`, historia, {
      observe: 'response',
    });
  }

  publish(id: number): Observable<HistoriaResponseType> {
    return this.http.post<IHistoria>(`${this.resourceUrl}/${id}/publicar`, null, {
      observe: 'response',
    });
  }

  unpublish(id: number): Observable<HistoriaResponseType> {
    return this.http.post<IHistoria>(`${this.resourceUrl}/${id}/despublicar`, null, {
      observe: 'response',
    });
  }

  addImage(historiaId: number, file: File, textoAlternativo: string | null, portada: boolean): Observable<HistoriaImagenResponseType> {
    const formData = new FormData();

    formData.append('file', file);
    formData.append('portada', String(portada));

    if (textoAlternativo?.trim()) {
      formData.append('textoAlternativo', textoAlternativo.trim());
    }

    return this.http.post<IHistoriaImagen>(`${this.resourceUrl}/${historiaId}/imagenes`, formData, {
      observe: 'response',
    });
  }

  getImage(historiaId: number, imagenId: number): Observable<HistoriaImageBlobResponseType> {
    return this.http.get(`${this.resourceUrl}/${historiaId}/imagenes/${imagenId}`, {
      observe: 'response',
      responseType: 'blob',
    });
  }

  delete(id: number): Observable<HttpResponse<unknown>> {
    return this.http.delete<unknown>(`${this.resourceUrl}/${id}`, {
      observe: 'response',
    });
  }

  deleteImage(historiaId: number, imagenId: number): Observable<HttpResponse<unknown>> {
    return this.http.delete<unknown>(`${this.resourceUrl}/${historiaId}/imagenes/${imagenId}`, {
      observe: 'response',
    });
  }

  setCover(historiaId: number, imagenId: number): Observable<HistoriaImagenResponseType> {
    return this.http.post<IHistoriaImagen>(`${this.resourceUrl}/${historiaId}/imagenes/${imagenId}/portada`, null, {
      observe: 'response',
    });
  }

  reorderImages(historiaId: number, imageIds: number[]): Observable<HttpResponse<unknown>> {
    return this.http.put<unknown>(`${this.resourceUrl}/${historiaId}/imagenes/orden`, imageIds, {
      observe: 'response',
    });
  }
}
