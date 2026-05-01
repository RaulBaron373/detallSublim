import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IProducto } from 'app/entities/producto/producto.model';
import { ProductoService } from 'app/entities/producto/service/producto.service';
import { EstadoSolicitud } from 'app/entities/enumerations/estado-solicitud.model';
import { SolicitudPresupuestoService } from '../service/solicitud-presupuesto.service';
import { ISolicitudPresupuesto } from '../solicitud-presupuesto.model';
import { SolicitudPresupuestoFormGroup, SolicitudPresupuestoFormService } from './solicitud-presupuesto-form.service';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'jhi-solicitud-presupuesto-update',
  templateUrl: './solicitud-presupuesto-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule, RouterLink],
})
export class SolicitudPresupuestoUpdateComponent implements OnInit {
  isSaving = false;
  solicitudPresupuesto: ISolicitudPresupuesto | null = null;
  estadoSolicitudValues = Object.keys(EstadoSolicitud);

  productosSharedCollection: IProducto[] = [];

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected solicitudPresupuestoService = inject(SolicitudPresupuestoService);
  protected solicitudPresupuestoFormService = inject(SolicitudPresupuestoFormService);
  protected productoService = inject(ProductoService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: SolicitudPresupuestoFormGroup = this.solicitudPresupuestoFormService.createSolicitudPresupuestoFormGroup();

  compareProducto = (o1: IProducto | null, o2: IProducto | null): boolean => this.productoService.compareProducto(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ solicitudPresupuesto }) => {
      this.solicitudPresupuesto = solicitudPresupuesto;
      if (solicitudPresupuesto) {
        this.updateForm(solicitudPresupuesto);
      }

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('detallSublimApp.error', { ...err, key: `error.file.${err.key}` })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const solicitudPresupuesto = this.solicitudPresupuestoFormService.getSolicitudPresupuesto(this.editForm);
    if (solicitudPresupuesto.id !== null) {
      this.subscribeToSaveResponse(this.solicitudPresupuestoService.update(solicitudPresupuesto));
    } else {
      this.subscribeToSaveResponse(this.solicitudPresupuestoService.create(solicitudPresupuesto));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISolicitudPresupuesto>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(solicitudPresupuesto: ISolicitudPresupuesto): void {
    this.solicitudPresupuesto = solicitudPresupuesto;
    this.solicitudPresupuestoFormService.resetForm(this.editForm, solicitudPresupuesto);

    this.productosSharedCollection = this.productoService.addProductoToCollectionIfMissing<IProducto>(
      this.productosSharedCollection,
      solicitudPresupuesto.producto,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.productoService
      .query()
      .pipe(map((res: HttpResponse<IProducto[]>) => res.body ?? []))
      .pipe(
        map((productos: IProducto[]) =>
          this.productoService.addProductoToCollectionIfMissing<IProducto>(productos, this.solicitudPresupuesto?.producto),
        ),
      )
      .subscribe((productos: IProducto[]) => (this.productosSharedCollection = productos));
  }
}
