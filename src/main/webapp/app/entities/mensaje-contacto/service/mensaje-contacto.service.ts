import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMensajeContacto, NewMensajeContacto } from '../mensaje-contacto.model';

export type PartialUpdateMensajeContacto = Partial<IMensajeContacto> & Pick<IMensajeContacto, 'id'>;

type RestOf<T extends IMensajeContacto | NewMensajeContacto> = Omit<T, 'fechaEnvio'> & {
  fechaEnvio?: string | null;
};

export type RestMensajeContacto = RestOf<IMensajeContacto>;

export type NewRestMensajeContacto = RestOf<NewMensajeContacto>;

export type PartialUpdateRestMensajeContacto = RestOf<PartialUpdateMensajeContacto>;

export type EntityResponseType = HttpResponse<IMensajeContacto>;
export type EntityArrayResponseType = HttpResponse<IMensajeContacto[]>;

@Injectable({ providedIn: 'root' })
export class MensajeContactoService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/mensaje-contactos');

  create(mensajeContacto: NewMensajeContacto): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(mensajeContacto);
    return this.http
      .post<RestMensajeContacto>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(mensajeContacto: IMensajeContacto): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(mensajeContacto);
    return this.http
      .put<RestMensajeContacto>(`${this.resourceUrl}/${this.getMensajeContactoIdentifier(mensajeContacto)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  responder(id: number, respuesta: string): Observable<any> {
    return this.http.post(`${this.resourceUrl}/${id}/responder`, respuesta);
  }

  partialUpdate(mensajeContacto: PartialUpdateMensajeContacto): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(mensajeContacto);
    return this.http
      .patch<RestMensajeContacto>(`${this.resourceUrl}/${this.getMensajeContactoIdentifier(mensajeContacto)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestMensajeContacto>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestMensajeContacto[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getMensajeContactoIdentifier(mensajeContacto: Pick<IMensajeContacto, 'id'>): number {
    return mensajeContacto.id;
  }

  compareMensajeContacto(o1: Pick<IMensajeContacto, 'id'> | null, o2: Pick<IMensajeContacto, 'id'> | null): boolean {
    return o1 && o2 ? this.getMensajeContactoIdentifier(o1) === this.getMensajeContactoIdentifier(o2) : o1 === o2;
  }

  addMensajeContactoToCollectionIfMissing<Type extends Pick<IMensajeContacto, 'id'>>(
    mensajeContactoCollection: Type[],
    ...mensajeContactosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const mensajeContactos: Type[] = mensajeContactosToCheck.filter(isPresent);
    if (mensajeContactos.length > 0) {
      const mensajeContactoCollectionIdentifiers = mensajeContactoCollection.map(mensajeContactoItem =>
        this.getMensajeContactoIdentifier(mensajeContactoItem),
      );
      const mensajeContactosToAdd = mensajeContactos.filter(mensajeContactoItem => {
        const mensajeContactoIdentifier = this.getMensajeContactoIdentifier(mensajeContactoItem);
        if (mensajeContactoCollectionIdentifiers.includes(mensajeContactoIdentifier)) {
          return false;
        }
        mensajeContactoCollectionIdentifiers.push(mensajeContactoIdentifier);
        return true;
      });
      return [...mensajeContactosToAdd, ...mensajeContactoCollection];
    }
    return mensajeContactoCollection;
  }

  protected convertDateFromClient<T extends IMensajeContacto | NewMensajeContacto | PartialUpdateMensajeContacto>(
    mensajeContacto: T,
  ): RestOf<T> {
    return {
      ...mensajeContacto,
      fechaEnvio: mensajeContacto.fechaEnvio?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restMensajeContacto: RestMensajeContacto): IMensajeContacto {
    return {
      ...restMensajeContacto,
      fechaEnvio: restMensajeContacto.fechaEnvio ? dayjs(restMensajeContacto.fechaEnvio) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestMensajeContacto>): HttpResponse<IMensajeContacto> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestMensajeContacto[]>): HttpResponse<IMensajeContacto[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
