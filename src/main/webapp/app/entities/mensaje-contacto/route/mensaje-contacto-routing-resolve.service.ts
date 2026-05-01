import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMensajeContacto } from '../mensaje-contacto.model';
import { MensajeContactoService } from '../service/mensaje-contacto.service';

const mensajeContactoResolve = (route: ActivatedRouteSnapshot): Observable<null | IMensajeContacto> => {
  const id = route.params.id;
  if (id) {
    return inject(MensajeContactoService)
      .find(id)
      .pipe(
        mergeMap((mensajeContacto: HttpResponse<IMensajeContacto>) => {
          if (mensajeContacto.body) {
            return of(mensajeContacto.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default mensajeContactoResolve;
