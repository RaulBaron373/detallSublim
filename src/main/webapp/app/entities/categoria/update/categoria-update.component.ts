import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { CategoriaService } from '../service/categoria.service';
import { ICategoria } from '../categoria.model';
import { CategoriaFormGroup, CategoriaFormService } from './categoria-form.service';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'jhi-categoria-update',
  templateUrl: './categoria-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule, RouterLink],
})
export class CategoriaUpdateComponent implements OnInit {
  isSaving = false;
  categoria: ICategoria | null = null;

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected categoriaService = inject(CategoriaService);
  protected categoriaFormService = inject(CategoriaFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CategoriaFormGroup = this.categoriaFormService.createCategoriaFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ categoria }) => {
      this.categoria = categoria;
      if (categoria) {
        this.updateForm(categoria);
      }
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
    const categoria = this.categoriaFormService.getCategoria(this.editForm);
    if (categoria.id !== null) {
      this.subscribeToSaveResponse(this.categoriaService.update(categoria));
    } else {
      this.subscribeToSaveResponse(this.categoriaService.create(categoria));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICategoria>>): void {
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

  protected updateForm(categoria: ICategoria): void {
    this.categoria = categoria;
    this.categoriaFormService.resetForm(this.editForm, categoria);
  }
}
