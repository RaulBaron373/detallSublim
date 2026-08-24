import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { forkJoin, Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';

import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';

import { ICategoria } from 'app/entities/categoria/categoria.model';
import { CategoriaService } from 'app/entities/categoria/service/categoria.service';

import { ProductoService } from '../service/producto.service';
import { IProducto, NewProducto } from '../producto.model';
import { ProductoFormGroup, ProductoFormService } from './producto-form.service';

@Component({
  selector: 'jhi-producto-update',
  templateUrl: './producto-update.component.html',
  styleUrl: './producto-update.component.scss',
  imports: [SharedModule, FormsModule, ReactiveFormsModule, RouterLink],
})
export class ProductoUpdateComponent implements OnInit {
  isSaving = false;

  producto: IProducto | null = null;

  categoriasSharedCollection: ICategoria[] = [];

  featuredProducts: IProducto[] = [];

  selectedFeaturedIds = new Set<number>();

  showFeaturedLimitModal = false;

  pendingFeaturedIdsToDisable: number[] = [];

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected productoService = inject(ProductoService);
  protected productoFormService = inject(ProductoFormService);
  protected categoriaService = inject(CategoriaService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ProductoFormGroup = this.productoFormService.createProductoFormGroup();

  compareCategoria = (o1: ICategoria | null, o2: ICategoria | null): boolean => this.categoriaService.compareCategoria(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ producto }) => {
      this.producto = producto;

      if (producto) {
        this.updateForm(producto);
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
        this.eventManager.broadcast(
          new EventWithContent<AlertError>('detallSublimApp.error', {
            ...err,
            key: `error.file.${err.key}`,
          }),
        ),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    const producto = this.productoFormService.getProducto(this.editForm);

    /*
     * Si el producto se quiere guardar como destacado
     * y todavía no se ha preparado ningún reemplazo,
     * comprobamos primero cuántos destacados existen.
     */
    if (producto.destacado && this.pendingFeaturedIdsToDisable.length === 0) {
      this.checkFeaturedLimitBeforeSave(producto);
      return;
    }

    this.persistProducto(producto);
  }

  onImageSelect(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (!input.files || input.files.length === 0) {
      return;
    }

    const file = input.files[0];
    const reader = new FileReader();

    reader.onload = () => {
      this.editForm.patchValue({
        imagenUrl: reader.result as string,
      });
    };

    reader.readAsDataURL(file);
  }

  /**
   * Se ejecuta cuando la administradora marca o
   * desmarca la casilla "Producto destacado".
   */
  onDestacadoChange(event: Event): void {
    const checked = (event.target as HTMLInputElement).checked;

    /*
     * Si se desmarca, no hace falta comprobar
     * ningún límite.
     */
    if (!checked) {
      this.pendingFeaturedIdsToDisable = [];
      this.showFeaturedLimitModal = false;
      this.featuredProducts = [];
      this.selectedFeaturedIds.clear();

      return;
    }

    const currentProductId = this.producto?.id ?? null;

    /*
     * Consultamos todos los productos para saber
     * cuántos están actualmente destacados.
     */
    this.productoService
      .query({
        size: 1000,
        sort: ['nombre,asc'],
      })
      .subscribe(res => {
        /*
         * Excluimos el producto que estamos editando,
         * porque no debe contarse dos veces.
         */
        this.featuredProducts = (res.body ?? []).filter(producto => producto.destacado && producto.id !== currentProductId);

        /*
         * Si ya existen tres o más destacados,
         * mostramos el modal.
         */
        if (this.featuredProducts.length >= 3) {
          this.selectedFeaturedIds = new Set(this.featuredProducts.map(producto => producto.id));

          this.showFeaturedLimitModal = true;
        }
      });
  }

  /**
   * Indica si un producto del modal continúa
   * seleccionado como destacado.
   */
  isFeaturedSelected(id: number): boolean {
    return this.selectedFeaturedIds.has(id);
  }

  /**
   * Marca o desmarca productos dentro del modal.
   *
   * Como el producto que estamos editando ocupará
   * una de las tres posiciones, solo podemos
   * conservar otros dos productos destacados.
   */
  toggleFeaturedSelection(id: number): void {
    if (this.selectedFeaturedIds.has(id)) {
      this.selectedFeaturedIds.delete(id);
      return;
    }

    if (this.selectedFeaturedIds.size >= 2) {
      return;
    }

    this.selectedFeaturedIds.add(id);
  }

  /**
   * Cancela la selección del nuevo producto
   * destacado.
   */
  cancelFeaturedReplacement(): void {
    this.showFeaturedLimitModal = false;

    this.pendingFeaturedIdsToDisable = [];

    this.featuredProducts = [];

    this.selectedFeaturedIds.clear();

    this.editForm.patchValue(
      {
        destacado: false,
      },
      {
        emitEvent: false,
      },
    );
  }

  /**
   * Confirma qué productos actuales dejarán de
   * estar destacados.
   *
   * Los cambios todavía no se guardan en la BD.
   * Se aplicarán cuando la administradora pulse
   * "Guardar".
   */
  confirmFeaturedReplacement(): void {
    /*
     * El nuevo producto ocupará una posición.
     * Como máximo pueden mantenerse otros dos.
     */
    if (this.selectedFeaturedIds.size > 2) {
      return;
    }

    this.pendingFeaturedIdsToDisable = this.featuredProducts
      .filter(producto => !this.selectedFeaturedIds.has(producto.id))
      .map(producto => producto.id);

    this.showFeaturedLimitModal = false;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProducto>>): void {
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

  protected updateForm(producto: IProducto): void {
    this.producto = producto;

    this.productoFormService.resetForm(this.editForm, producto);

    this.categoriasSharedCollection = this.categoriaService.addCategoriaToCollectionIfMissing<ICategoria>(
      this.categoriasSharedCollection,
      producto.categoria,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.categoriaService
      .query()
      .pipe(map((res: HttpResponse<ICategoria[]>) => res.body ?? []))
      .pipe(
        map((categorias: ICategoria[]) =>
          this.categoriaService.addCategoriaToCollectionIfMissing<ICategoria>(categorias, this.producto?.categoria),
        ),
      )
      .subscribe((categorias: ICategoria[]) => (this.categoriasSharedCollection = categorias));
  }

  /**
   * Segunda comprobación antes de guardar.
   *
   * Aunque por algún motivo no se hubiese
   * disparado el evento change del checkbox,
   * aquí volvemos a verificar el límite.
   */
  private checkFeaturedLimitBeforeSave(producto: IProducto | NewProducto): void {
    const currentProductId = producto.id;

    this.productoService
      .query({
        size: 1000,
        sort: ['nombre,asc'],
      })
      .subscribe(res => {
        this.featuredProducts = (res.body ?? []).filter(item => item.destacado && item.id !== currentProductId);

        /*
         * Ya existen tres destacados distintos
         * del producto actual.
         */
        if (this.featuredProducts.length >= 3) {
          this.selectedFeaturedIds = new Set(this.featuredProducts.map(item => item.id));

          this.showFeaturedLimitModal = true;

          return;
        }

        /*
         * Todavía quedan posiciones disponibles.
         */
        this.persistProducto(producto);
      });
  }

  /**
   * Guarda el producto.
   *
   * Si previamente se ha realizado un reemplazo,
   * primero desactiva como destacados los productos
   * que la administradora haya desmarcado.
   */
  private persistProducto(producto: IProducto | NewProducto): void {
    this.isSaving = true;

    if (producto.destacado && this.pendingFeaturedIdsToDisable.length > 0) {
      const updates = this.pendingFeaturedIdsToDisable.map(id =>
        this.productoService.partialUpdate({
          id,
          destacado: false,
        }),
      );

      forkJoin(updates).subscribe({
        next: () => {
          this.pendingFeaturedIdsToDisable = [];

          this.saveCurrentProducto(producto);
        },

        error: () => {
          this.onSaveFinalize();
        },
      });

      return;
    }

    this.saveCurrentProducto(producto);
  }

  /**
   * Crea o actualiza el producto actual.
   */
  private saveCurrentProducto(producto: IProducto | NewProducto): void {
    if (producto.id !== null) {
      this.subscribeToSaveResponse(this.productoService.update(producto));

      return;
    }

    this.subscribeToSaveResponse(this.productoService.create(producto));
  }
}
