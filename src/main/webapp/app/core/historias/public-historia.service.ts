import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';

import { IHistoria, IHistoriaResumen } from './historia.model';

export type HistoriaResumenArrayResponseType = HttpResponse<IHistoriaResumen[]>;

@Injectable({
  providedIn: 'root',
})
export class PublicHistoriaService {
  private readonly http = inject(HttpClient);
  private readonly applicationConfigService = inject(ApplicationConfigService);

  private readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/public/historias');

  query(req?: { page?: number; size?: number; sort?: string | string[] }): Observable<HistoriaResumenArrayResponseType> {
    const options = createRequestOption(req);

    return this.http.get<IHistoriaResumen[]>(this.resourceUrl, {
      params: options,
      observe: 'response',
    });
  }

  findBySlug(slug: string): Observable<IHistoria> {
    return this.http.get<IHistoria>(`${this.resourceUrl}/${encodeURIComponent(slug)}`);
  }
}
