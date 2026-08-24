import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISolicitudPresupuesto, NewSolicitudPresupuesto } from '../solicitud-presupuesto.model';

export type PartialUpdateSolicitudPresupuesto = Partial<ISolicitudPresupuesto> & Pick<ISolicitudPresupuesto, 'id'>;

type RestOf<T extends ISolicitudPresupuesto | NewSolicitudPresupuesto> = Omit<T, 'fechaSolicitud' | 'fechaEnvioPresupuesto'> & {
  fechaSolicitud?: string | null;
  fechaEnvioPresupuesto?: string | null;
};

export type RestSolicitudPresupuesto = RestOf<ISolicitudPresupuesto>;

export type NewRestSolicitudPresupuesto = RestOf<NewSolicitudPresupuesto>;

export type PartialUpdateRestSolicitudPresupuesto = RestOf<PartialUpdateSolicitudPresupuesto>;

export type EntityResponseType = HttpResponse<ISolicitudPresupuesto>;
export type EntityArrayResponseType = HttpResponse<ISolicitudPresupuesto[]>;

@Injectable({ providedIn: 'root' })
export class SolicitudPresupuestoService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/solicitud-presupuestos');

  create(solicitudPresupuesto: NewSolicitudPresupuesto): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(solicitudPresupuesto);
    return this.http
      .post<RestSolicitudPresupuesto>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(solicitudPresupuesto: ISolicitudPresupuesto): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(solicitudPresupuesto);
    return this.http
      .put<RestSolicitudPresupuesto>(`${this.resourceUrl}/${this.getSolicitudPresupuestoIdentifier(solicitudPresupuesto)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(solicitudPresupuesto: PartialUpdateSolicitudPresupuesto): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(solicitudPresupuesto);
    return this.http
      .patch<RestSolicitudPresupuesto>(`${this.resourceUrl}/${this.getSolicitudPresupuestoIdentifier(solicitudPresupuesto)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestSolicitudPresupuesto>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestSolicitudPresupuesto[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getSolicitudPresupuestoIdentifier(solicitudPresupuesto: Pick<ISolicitudPresupuesto, 'id'>): number {
    return solicitudPresupuesto.id;
  }

  compareSolicitudPresupuesto(o1: Pick<ISolicitudPresupuesto, 'id'> | null, o2: Pick<ISolicitudPresupuesto, 'id'> | null): boolean {
    return o1 && o2 ? this.getSolicitudPresupuestoIdentifier(o1) === this.getSolicitudPresupuestoIdentifier(o2) : o1 === o2;
  }

  addSolicitudPresupuestoToCollectionIfMissing<Type extends Pick<ISolicitudPresupuesto, 'id'>>(
    solicitudPresupuestoCollection: Type[],
    ...solicitudPresupuestosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const solicitudPresupuestos: Type[] = solicitudPresupuestosToCheck.filter(isPresent);
    if (solicitudPresupuestos.length > 0) {
      const solicitudPresupuestoCollectionIdentifiers = solicitudPresupuestoCollection.map(solicitudPresupuestoItem =>
        this.getSolicitudPresupuestoIdentifier(solicitudPresupuestoItem),
      );
      const solicitudPresupuestosToAdd = solicitudPresupuestos.filter(solicitudPresupuestoItem => {
        const solicitudPresupuestoIdentifier = this.getSolicitudPresupuestoIdentifier(solicitudPresupuestoItem);
        if (solicitudPresupuestoCollectionIdentifiers.includes(solicitudPresupuestoIdentifier)) {
          return false;
        }
        solicitudPresupuestoCollectionIdentifiers.push(solicitudPresupuestoIdentifier);
        return true;
      });
      return [...solicitudPresupuestosToAdd, ...solicitudPresupuestoCollection];
    }
    return solicitudPresupuestoCollection;
  }

  protected convertDateFromClient<T extends ISolicitudPresupuesto | NewSolicitudPresupuesto | PartialUpdateSolicitudPresupuesto>(
    solicitudPresupuesto: T,
  ): RestOf<T> {
    return {
      ...solicitudPresupuesto,
      fechaSolicitud: solicitudPresupuesto.fechaSolicitud?.toJSON() ?? null,
      fechaEnvioPresupuesto: solicitudPresupuesto.fechaEnvioPresupuesto?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restSolicitudPresupuesto: RestSolicitudPresupuesto): ISolicitudPresupuesto {
    return {
      ...restSolicitudPresupuesto,

      fechaSolicitud: restSolicitudPresupuesto.fechaSolicitud ? dayjs(restSolicitudPresupuesto.fechaSolicitud) : undefined,

      fechaEnvioPresupuesto: restSolicitudPresupuesto.fechaEnvioPresupuesto
        ? dayjs(restSolicitudPresupuesto.fechaEnvioPresupuesto)
        : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestSolicitudPresupuesto>): HttpResponse<ISolicitudPresupuesto> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestSolicitudPresupuesto[]>): HttpResponse<ISolicitudPresupuesto[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
