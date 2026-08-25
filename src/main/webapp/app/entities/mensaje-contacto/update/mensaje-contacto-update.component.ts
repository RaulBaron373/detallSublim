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
import { MensajeContactoService } from '../service/mensaje-contacto.service';
import { IMensajeContacto } from '../mensaje-contacto.model';
import { MensajeContactoFormGroup, MensajeContactoFormService } from './mensaje-contacto-form.service';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'jhi-mensaje-contacto-update',
  templateUrl: './mensaje-contacto-update.component.html',
  styleUrl: './mensaje-contacto-update.component.scss',
  imports: [SharedModule, FormsModule, ReactiveFormsModule, RouterLink],
})
export class MensajeContactoUpdateComponent implements OnInit {
  isSaving = false;
  mensajeContacto: IMensajeContacto | null = null;

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected mensajeContactoService = inject(MensajeContactoService);
  protected mensajeContactoFormService = inject(MensajeContactoFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: MensajeContactoFormGroup = this.mensajeContactoFormService.createMensajeContactoFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ mensajeContacto }) => {
      this.mensajeContacto = mensajeContacto;
      if (mensajeContacto) {
        this.updateForm(mensajeContacto);
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
    const mensajeContacto = this.mensajeContactoFormService.getMensajeContacto(this.editForm);
    if (mensajeContacto.id !== null) {
      this.subscribeToSaveResponse(this.mensajeContactoService.update(mensajeContacto));
    } else {
      this.subscribeToSaveResponse(this.mensajeContactoService.create(mensajeContacto));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMensajeContacto>>): void {
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

  protected updateForm(mensajeContacto: IMensajeContacto): void {
    this.mensajeContacto = mensajeContacto;
    this.mensajeContactoFormService.resetForm(this.editForm, mensajeContacto);
  }
}
