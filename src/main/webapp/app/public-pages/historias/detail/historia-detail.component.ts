import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';

import SharedModule from 'app/shared/shared.module';
import { IHistoria, IHistoriaImagen } from 'app/core/historias/historia.model';
import { PublicHistoriaService } from 'app/core/historias/public-historia.service';

type HistoriaMediaLayout = 'wide' | 'offset-left' | 'offset-right';

interface HistoriaStoryImage {
  image: IHistoriaImagen;
  layout: HistoriaMediaLayout;
}

interface HistoriaStorySection {
  id: number;
  paragraph: string;
  images: HistoriaStoryImage[];
}

@Component({
  selector: 'jhi-historia-detail',
  standalone: true,
  imports: [SharedModule, RouterLink],
  templateUrl: './historia-detail.component.html',
  styleUrl: './historia-detail.component.scss',
})
export class HistoriaDetailComponent implements OnInit {
  historia: IHistoria | null = null;
  storySections: HistoriaStorySection[] = [];

  isLoading = false;
  hasError = false;
  notFound = false;

  private slug = '';

  constructor(
    private readonly route: ActivatedRoute,
    private readonly publicHistoriaService: PublicHistoriaService,
  ) {}

  ngOnInit(): void {
    const slug = this.route.snapshot.paramMap.get('slug');

    if (!slug) {
      this.notFound = true;
      return;
    }

    this.slug = slug;
    this.loadHistoria();
  }

  get portada(): IHistoriaImagen | null {
    if (!this.historia?.imagenes.length) {
      return null;
    }

    return this.historia.imagenes.find(imagen => imagen.portada) ?? [...this.historia.imagenes].sort((a, b) => a.orden - b.orden)[0];
  }

  get imagenesSecundarias(): IHistoriaImagen[] {
    if (!this.historia) {
      return [];
    }

    const portadaId = this.portada?.id;

    return [...this.historia.imagenes].filter(imagen => imagen.id !== portadaId).sort((a, b) => a.orden - b.orden);
  }

  loadHistoria(): void {
    if (!this.slug) {
      return;
    }

    this.isLoading = true;
    this.hasError = false;
    this.notFound = false;

    this.publicHistoriaService
      .findBySlug(this.slug)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: historia => {
          this.historia = historia;
          this.storySections = this.buildStorySections(historia);
        },
        error: (error: HttpErrorResponse) => {
          this.historia = null;
          this.storySections = [];

          if (error.status === 404) {
            this.notFound = true;
            return;
          }

          this.hasError = true;
        },
      });
  }

  private buildStorySections(historia: IHistoria): HistoriaStorySection[] {
    const paragraphs = this.splitContent(historia.contenido);
    const portadaId =
      historia.imagenes.find(imagen => imagen.portada)?.id ?? [...historia.imagenes].sort((a, b) => a.orden - b.orden)[0]?.id;

    const secondaryImages = [...historia.imagenes]
      .filter(imagen => imagen.id !== portadaId && Boolean(imagen.url))
      .sort((a, b) => a.orden - b.orden);

    const sections: HistoriaStorySection[] = paragraphs.map((paragraph, index) => ({
      id: index,
      paragraph,
      images: [],
    }));

    if (sections.length === 0) {
      return [];
    }

    secondaryImages.forEach((image, index) => {
      const targetSection = Math.min(
        sections.length - 1,
        Math.max(0, Math.round(((index + 1) * sections.length) / (secondaryImages.length + 1)) - 1),
      );

      const layout: HistoriaMediaLayout = index % 3 === 0 ? 'offset-right' : index % 3 === 1 ? 'offset-left' : 'wide';

      sections[targetSection].images.push({
        image,
        layout,
      });
    });

    return sections;
  }

  private splitContent(content: string): string[] {
    const normalized = content.trim();

    if (!normalized) {
      return [];
    }

    let paragraphs = normalized
      .split(/\r?\n\s*\r?\n/)
      .map(paragraph => paragraph.trim())
      .filter(Boolean);

    if (paragraphs.length === 1 && /\r?\n/.test(normalized)) {
      paragraphs = normalized
        .split(/\r?\n+/)
        .map(paragraph => paragraph.trim())
        .filter(Boolean);
    }

    if (paragraphs.length === 1 && normalized.length > 750) {
      paragraphs = this.chunkLongParagraph(normalized);
    }

    return paragraphs;
  }

  private chunkLongParagraph(content: string): string[] {
    const sentences =
      content
        .match(/[^.!?]+[.!?]+|[^.!?]+$/g)
        ?.map(sentence => sentence.trim())
        .filter(Boolean) ?? [];

    if (sentences.length <= 1) {
      return [content];
    }

    const chunks: string[] = [];
    let currentChunk = '';

    for (const sentence of sentences) {
      const candidate = currentChunk ? `${currentChunk} ${sentence}` : sentence;

      if (candidate.length > 620 && currentChunk) {
        chunks.push(currentChunk);
        currentChunk = sentence;
      } else {
        currentChunk = candidate;
      }
    }

    if (currentChunk) {
      chunks.push(currentChunk);
    }

    return chunks;
  }
}
