import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';

import SharedModule from 'app/shared/shared.module';
import { IHistoriaResumen } from 'app/core/historias/historia.model';
import { PublicHistoriaService } from 'app/core/historias/public-historia.service';

@Component({
  selector: 'jhi-historias',
  standalone: true,
  imports: [SharedModule, RouterLink],
  templateUrl: './historias.component.html',
  styleUrl: './historias.component.scss',
})
export class HistoriasComponent implements OnInit {
  historias: IHistoriaResumen[] = [];

  isLoading = false;
  hasError = false;

  page = 1;
  itemsPerPage = 9;
  totalItems = 0;

  constructor(private readonly publicHistoriaService: PublicHistoriaService) {}

  ngOnInit(): void {
    this.loadPage();
  }

  loadPage(page = this.page): void {
    this.page = page;
    this.isLoading = true;
    this.hasError = false;

    this.publicHistoriaService
      .query({
        page: this.page - 1,
        size: this.itemsPerPage,
        sort: 'fechaPublicacion,desc',
      })
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: response => {
          this.historias = response.body ?? [];

          const totalItemsHeader = response.headers.get('X-Total-Count');

          this.totalItems = totalItemsHeader ? Number(totalItemsHeader) : this.historias.length;
        },
        error: () => {
          this.historias = [];
          this.totalItems = 0;
          this.hasError = true;
        },
      });
  }
}
