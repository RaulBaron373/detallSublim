import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';

import { AdminHistoriaService } from 'app/core/historias/admin-historia.service';
import { IHistoria } from 'app/core/historias/historia.model';
import SharedModule from 'app/shared/shared.module';

@Component({
  selector: 'jhi-historias-admin',
  standalone: true,
  imports: [SharedModule, RouterLink],
  templateUrl: './historias-admin.component.html',
  styleUrl: './historias-admin.component.scss',
})
export class HistoriasAdminComponent implements OnInit {
  historias: IHistoria[] = [];

  isLoading = false;
  hasError = false;

  page = 1;
  itemsPerPage = 10;
  totalItems = 0;

  changingStateId: number | null = null;
  stateChangeError = '';

  constructor(private readonly adminHistoriaService: AdminHistoriaService) {}

  ngOnInit(): void {
    this.loadPage();
  }

  togglePublication(historia: IHistoria): void {
    if (this.changingStateId !== null) {
      return;
    }

    this.changingStateId = historia.id;
    this.stateChangeError = '';

    const request$ =
      historia.estado === 'PUBLICADA' ? this.adminHistoriaService.unpublish(historia.id) : this.adminHistoriaService.publish(historia.id);

    request$.pipe(finalize(() => (this.changingStateId = null))).subscribe({
      next: response => {
        const updatedHistoria = response.body;

        if (!updatedHistoria) {
          this.loadPage();
          return;
        }

        this.historias = this.historias.map(currentHistoria =>
          currentHistoria.id === updatedHistoria.id ? updatedHistoria : currentHistoria,
        );
      },
      error: () => {
        this.stateChangeError =
          historia.estado === 'PUBLICADA'
            ? `No se pudo pasar "${historia.titulo}" a borrador. Inténtalo de nuevo.`
            : `No se pudo publicar "${historia.titulo}". Comprueba que tenga título, resumen, contenido y exactamente una portada.`;
      },
    });
  }

  loadPage(page = this.page): void {
    this.page = page;
    this.isLoading = true;
    this.hasError = false;

    this.adminHistoriaService
      .query({
        page: this.page - 1,
        size: this.itemsPerPage,
        sort: 'fechaActualizacion,desc',
      })
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: response => {
          this.historias = response.body ?? [];

          const totalItemsHeader = response.headers.get('X-Total-Count');
          const parsedTotalItems = totalItemsHeader ? Number(totalItemsHeader) : this.historias.length;

          this.totalItems = Number.isFinite(parsedTotalItems) ? parsedTotalItems : this.historias.length;
        },
        error: () => {
          this.historias = [];
          this.totalItems = 0;
          this.hasError = true;
        },
      });
  }
}
