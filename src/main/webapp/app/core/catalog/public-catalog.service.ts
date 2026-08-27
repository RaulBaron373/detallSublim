import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { ICategoria } from 'app/entities/categoria/categoria.model';
import { IProducto } from 'app/entities/producto/producto.model';

@Injectable({
  providedIn: 'root',
})
export class PublicCatalogService {
  private readonly http = inject(HttpClient);
  private readonly applicationConfigService = inject(ApplicationConfigService);
  getProductos(): Observable<IProducto[]> {
    return this.http.get<IProducto[]>(this.applicationConfigService.getEndpointFor('api/public/catalog/productos'));
  }
  getCategorias(): Observable<ICategoria[]> {
    return this.http.get<ICategoria[]>(this.applicationConfigService.getEndpointFor('api/public/catalog/categorias'));
  }
}
