import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISolicitudPresupuesto } from '../solicitud-presupuesto.model';
import { SolicitudPresupuestoService } from '../service/solicitud-presupuesto.service';

const solicitudPresupuestoResolve = (route: ActivatedRouteSnapshot): Observable<null | ISolicitudPresupuesto> => {
  const id = route.params.id;
  if (id) {
    return inject(SolicitudPresupuestoService)
      .find(id)
      .pipe(
        mergeMap((solicitudPresupuesto: HttpResponse<ISolicitudPresupuesto>) => {
          if (solicitudPresupuesto.body) {
            return of(solicitudPresupuesto.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default solicitudPresupuestoResolve;
