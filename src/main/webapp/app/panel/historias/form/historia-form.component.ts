import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';

import { AdminHistoriaService } from 'app/core/historias/admin-historia.service';
import { IHistoria, IHistoriaEditable, IHistoriaImagen } from 'app/core/historias/historia.model';
import SharedModule from 'app/shared/shared.module';

@Component({
  selector: 'jhi-historia-form',
  standalone: true,
  imports: [SharedModule, ReactiveFormsModule, RouterLink],
  templateUrl: './historia-form.component.html',
  styleUrl: './historia-form.component.scss',
})
export class HistoriaFormComponent implements OnInit, OnDestroy {
  historia: IHistoria | null = null;
  historiaId: number | null = null;

  isLoading = false;
  isSaving = false;
  hasLoadError = false;
  saveError = false;
  selectedImage: File | null = null;
  imageAltText = '';
  imageAsCover = false;

  isUploadingImage = false;
  imageError = '';
  imageActionId: number | null = null;
  pendingDeleteImageId: number | null = null;
  isReorderingImages = false;
  isChangingPublication = false;
  publicationError = '';
  deleteHistoriaRequested = false;
  isDeletingHistoria = false;
  deleteHistoriaError = '';

  readonly form = inject(FormBuilder).nonNullable.group({
    titulo: ['', [Validators.required, Validators.maxLength(160)]],
    resumen: ['', [Validators.required, Validators.maxLength(300)]],
    contenido: ['', [Validators.required, Validators.maxLength(20000)]],
  });

  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly adminHistoriaService = inject(AdminHistoriaService);
  private readonly maxImageSize = 5 * 1024 * 1024;
  private readonly allowedImageTypes = new Set(['image/jpeg', 'image/png', 'image/webp']);
  private readonly imageObjectUrls = new Map<number, string>();

  get isEditMode(): boolean {
    return this.historiaId !== null;
  }

  get hasSingleCover(): boolean {
    return this.images.filter(image => image.portada).length === 1;
  }

  get canPublish(): boolean {
    return (
      this.historia !== null &&
      this.historia.estado === 'BORRADOR' &&
      this.form.valid &&
      !this.form.dirty &&
      this.hasSingleCover &&
      !this.isSaving &&
      !this.isUploadingImage &&
      this.imageActionId === null &&
      !this.isReorderingImages &&
      !this.isChangingPublication
    );
  }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');

    if (!idParam) {
      return;
    }

    const parsedId = Number(idParam);

    if (!Number.isInteger(parsedId) || parsedId <= 0) {
      this.hasLoadError = true;
      return;
    }

    this.historiaId = parsedId;
    this.loadHistoria();
  }

  ngOnDestroy(): void {
    this.clearImagePreviews();
  }

  loadHistoria(): void {
    if (this.historiaId === null) {
      return;
    }

    this.isLoading = true;
    this.hasLoadError = false;

    this.adminHistoriaService
      .find(this.historiaId)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: response => {
          const historia = response.body;

          if (!historia) {
            this.hasLoadError = true;
            return;
          }

          this.historia = historia;

          this.form.patchValue({
            titulo: historia.titulo,
            resumen: historia.resumen,
            contenido: historia.contenido,
          });
          this.loadImagePreviews(historia);
        },
        error: () => {
          this.historia = null;
          this.hasLoadError = true;
        },
      });
  }

  get images(): IHistoriaImagen[] {
    return [...(this.historia?.imagenes ?? [])].sort((a, b) => a.orden - b.orden);
  }

  imagePreviewUrl(imageId: number): string | null {
    return this.imageObjectUrls.get(imageId) ?? null;
  }

  onImageSelected(event: Event): void {
    this.imageError = '';
    this.selectedImage = null;

    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    if (!file) {
      return;
    }

    if (!this.allowedImageTypes.has(file.type)) {
      this.imageError = 'Formato no permitido. Utiliza una imagen JPG, PNG o WebP.';
      input.value = '';
      return;
    }

    if (file.size > this.maxImageSize) {
      this.imageError = 'La imagen no puede superar los 5 MB.';
      input.value = '';
      return;
    }

    this.selectedImage = file;
  }

  uploadImage(fileInput: HTMLInputElement): void {
    const historiaId = this.historiaId;

    if (historiaId === null || !this.selectedImage || this.isUploadingImage) {
      return;
    }

    if (this.imageAltText.trim().length > 160) {
      this.imageError = 'El texto alternativo no puede superar los 160 caracteres.';
      return;
    }

    if ((this.historia?.imagenes.length ?? 0) >= 10) {
      this.imageError = 'Una historia no puede contener más de 10 imágenes.';
      return;
    }

    this.isUploadingImage = true;
    this.imageError = '';

    this.adminHistoriaService
      .addImage(historiaId, this.selectedImage, this.imageAltText.trim() || null, this.imageAsCover)
      .pipe(finalize(() => (this.isUploadingImage = false)))
      .subscribe({
        next: response => {
          const newImage = response.body;

          if (!newImage || !this.historia) {
            this.imageError = 'No se pudo recuperar la imagen guardada.';
            return;
          }

          let currentImages = this.historia.imagenes;

          if (newImage.portada) {
            currentImages = currentImages.map(image => ({
              ...image,
              portada: false,
            }));
          }

          this.historia = {
            ...this.historia,
            imagenes: [...currentImages, newImage],
          };

          this.loadImagePreview(historiaId, newImage.id);

          this.selectedImage = null;
          this.imageAltText = '';
          this.imageAsCover = false;
          fileInput.value = '';
        },
        error: () => {
          this.imageError = 'No se pudo subir la imagen. Comprueba el archivo e inténtalo de nuevo.';
        },
      });
  }

  setCover(image: IHistoriaImagen): void {
    const historiaId = this.historiaId;

    if (historiaId === null || !this.historia || image.portada || this.imageActionId !== null || this.isReorderingImages) {
      return;
    }

    this.imageActionId = image.id;
    this.imageError = '';

    this.adminHistoriaService
      .setCover(historiaId, image.id)
      .pipe(finalize(() => (this.imageActionId = null)))
      .subscribe({
        next: () => {
          if (!this.historia) {
            return;
          }

          this.historia = {
            ...this.historia,
            imagenes: this.historia.imagenes.map(currentImage => ({
              ...currentImage,
              portada: currentImage.id === image.id,
            })),
          };
        },
        error: () => {
          this.imageError = 'No se pudo establecer la imagen como portada.';
        },
      });
  }

  requestDeleteImage(imageId: number): void {
    if (this.imageActionId !== null || this.isReorderingImages) {
      return;
    }

    this.pendingDeleteImageId = imageId;
    this.imageError = '';
  }

  cancelDeleteImage(): void {
    this.pendingDeleteImageId = null;
  }

  deleteImage(image: IHistoriaImagen): void {
    const historiaId = this.historiaId;

    if (historiaId === null || !this.historia || this.imageActionId !== null || this.isReorderingImages) {
      return;
    }

    if (image.portada && this.historia.estado === 'PUBLICADA') {
      this.pendingDeleteImageId = null;
      this.imageError = 'Antes de eliminar la portada de una historia publicada, selecciona otra imagen como portada.';
      return;
    }

    this.imageActionId = image.id;
    this.imageError = '';

    this.adminHistoriaService
      .deleteImage(historiaId, image.id)
      .pipe(
        finalize(() => {
          this.imageActionId = null;
          this.pendingDeleteImageId = null;
        }),
      )
      .subscribe({
        next: () => {
          if (!this.historia) {
            return;
          }

          const previewUrl = this.imageObjectUrls.get(image.id);

          if (previewUrl) {
            URL.revokeObjectURL(previewUrl);
            this.imageObjectUrls.delete(image.id);
          }

          const remainingImages = this.images
            .filter(currentImage => currentImage.id !== image.id)
            .map((currentImage, index) => ({
              ...currentImage,
              orden: index,
            }));

          this.historia = {
            ...this.historia,
            imagenes: remainingImages,
          };
        },
        error: () => {
          this.imageError = 'No se pudo eliminar la imagen.';
        },
      });
  }

  moveImage(index: number, direction: -1 | 1): void {
    const historiaId = this.historiaId;

    if (historiaId === null || !this.historia || this.imageActionId !== null || this.isReorderingImages) {
      return;
    }

    const orderedImages = this.images;
    const targetIndex = index + direction;

    if (targetIndex < 0 || targetIndex >= orderedImages.length) {
      return;
    }

    const reorderedImages = [...orderedImages];
    [reorderedImages[index], reorderedImages[targetIndex]] = [reorderedImages[targetIndex], reorderedImages[index]];

    this.isReorderingImages = true;
    this.imageError = '';

    this.adminHistoriaService
      .reorderImages(
        historiaId,
        reorderedImages.map(image => image.id),
      )
      .pipe(finalize(() => (this.isReorderingImages = false)))
      .subscribe({
        next: () => {
          if (!this.historia) {
            return;
          }

          this.historia = {
            ...this.historia,
            imagenes: reorderedImages.map((image, order) => ({
              ...image,
              orden: order,
            })),
          };
        },
        error: () => {
          this.imageError = 'No se pudo guardar el nuevo orden de las imágenes.';
        },
      });
  }

  publish(): void {
    const historiaId = this.historiaId;

    if (historiaId === null || !this.historia || !this.canPublish) {
      return;
    }

    this.isChangingPublication = true;
    this.publicationError = '';

    this.adminHistoriaService
      .publish(historiaId)
      .pipe(finalize(() => (this.isChangingPublication = false)))
      .subscribe({
        next: response => {
          const publishedHistoria = response.body;

          if (!publishedHistoria) {
            this.publicationError = 'No se pudo recuperar la historia publicada.';
            return;
          }

          this.historia = publishedHistoria;
          this.form.markAsPristine();
        },
        error: () => {
          this.publicationError =
            'No se pudo publicar la historia. Comprueba que tenga contenido guardado y exactamente una imagen de portada.';
        },
      });
  }

  unpublish(): void {
    const historiaId = this.historiaId;

    if (historiaId === null || this.historia?.estado !== 'PUBLICADA' || this.form.dirty || this.isChangingPublication) {
      return;
    }

    this.isChangingPublication = true;
    this.publicationError = '';

    this.adminHistoriaService
      .unpublish(historiaId)
      .pipe(finalize(() => (this.isChangingPublication = false)))
      .subscribe({
        next: response => {
          const unpublishedHistoria = response.body;

          if (!unpublishedHistoria) {
            this.publicationError = 'No se pudo recuperar la historia después de despublicarla.';
            return;
          }

          this.historia = unpublishedHistoria;
          this.form.markAsPristine();
        },
        error: () => {
          this.publicationError = 'No se pudo despublicar la historia.';
        },
      });
  }

  requestDeleteHistoria(): void {
    if (!this.isEditMode || this.isDeletingHistoria) {
      return;
    }

    this.deleteHistoriaRequested = true;
    this.deleteHistoriaError = '';
  }

  cancelDeleteHistoria(): void {
    if (this.isDeletingHistoria) {
      return;
    }

    this.deleteHistoriaRequested = false;
    this.deleteHistoriaError = '';
  }

  deleteHistoria(): void {
    const historiaId = this.historiaId;

    if (historiaId === null || this.isDeletingHistoria) {
      return;
    }

    this.isDeletingHistoria = true;
    this.deleteHistoriaError = '';

    this.adminHistoriaService
      .delete(historiaId)
      .pipe(finalize(() => (this.isDeletingHistoria = false)))
      .subscribe({
        next: () => {
          this.deleteHistoriaRequested = false;
          void this.router.navigate(['/panel/historias']);
        },
        error: () => {
          this.deleteHistoriaError = 'No se pudo eliminar la historia. Inténtalo de nuevo.';
        },
      });
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const payload: IHistoriaEditable = this.form.getRawValue();

    this.isSaving = true;
    this.saveError = false;

    const request$ =
      this.historiaId === null ? this.adminHistoriaService.create(payload) : this.adminHistoriaService.update(this.historiaId, payload);

    request$.pipe(finalize(() => (this.isSaving = false))).subscribe({
      next: response => {
        const savedHistoria = response.body;

        if (!savedHistoria) {
          this.saveError = true;
          return;
        }

        this.historia = savedHistoria;
        this.form.markAsPristine();

        if (this.historiaId === null) {
          this.historiaId = savedHistoria.id;

          void this.router.navigate(['/panel/historias', savedHistoria.id, 'editar'], {
            replaceUrl: true,
          });
        }
      },
      error: () => {
        this.saveError = true;
      },
    });
  }

  private loadImagePreviews(historia: IHistoria): void {
    this.clearImagePreviews();

    for (const image of historia.imagenes) {
      this.loadImagePreview(historia.id, image.id);
    }
  }

  private loadImagePreview(historiaId: number, imageId: number): void {
    this.adminHistoriaService.getImage(historiaId, imageId).subscribe({
      next: response => {
        const blob = response.body;

        if (!blob) {
          return;
        }

        const currentUrl = this.imageObjectUrls.get(imageId);

        if (currentUrl) {
          URL.revokeObjectURL(currentUrl);
        }

        this.imageObjectUrls.set(imageId, URL.createObjectURL(blob));
      },
      error: () => {
        const currentUrl = this.imageObjectUrls.get(imageId);

        if (currentUrl) {
          URL.revokeObjectURL(currentUrl);
          this.imageObjectUrls.delete(imageId);
        }
      },
    });
  }

  private clearImagePreviews(): void {
    this.imageObjectUrls.forEach(url => URL.revokeObjectURL(url));
    this.imageObjectUrls.clear();
  }
}
