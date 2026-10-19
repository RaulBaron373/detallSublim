import { Component, OnDestroy, OnInit, inject, signal } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { IProducto } from 'app/entities/producto/producto.model';
import { PublicCatalogService } from 'app/core/catalog/public-catalog.service';

@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
  imports: [SharedModule, RouterModule],
})
export default class HomeComponent implements OnInit, OnDestroy {
  account = signal<Account | null>(null);
  featuredProducts: IProducto[] = [];

  // Contenido de Inicio únicamente en español
  readonly t = {
    badge: 'Personalización Premium',

    heroTitle: 'Personaliza',
    heroHighlight: 'lo que quieras',
    heroDescription:
      'Transformamos productos ordinarios en piezas únicas con acabados profesionales para particulares, eventos y empresas.',

    quoteButton: 'Solicita tu presupuesto',
    catalogButton: 'Ver productos',

    technologiesTitle: 'Nuestras Tecnologías',
    technologiesSubtitle: 'Utilizamos técnicas avanzadas para conseguir acabados duraderos, profesionales y adaptados a cada producto.',

    technologies: [
      {
        icon: 'sparkles',
        iconClass: 'ds-tech-icon-pink',
        title: 'Sublimación',
        description:
          'Técnica ideal para productos como tazas, textiles específicos y artículos promocionales con impresión a todo color y gran definición.',
      },
      {
        icon: 'shirt',
        iconClass: 'ds-tech-icon-cyan',
        title: 'DTF textil',
        description:
          'Solución adecuada para camisetas, sudaderas y otras prendas personalizadas, adaptándose al diseño y al tipo de tejido.',
      },
      {
        icon: 'printer',
        iconClass: 'ds-tech-icon-yellow',
        title: 'DTF UV',
        description:
          'Personalización a todo color mediante transferencia de diseños sobre superficies rígidas como vidrio, metal, plástico o madera, con gran definición y acabados llamativos.',
      },
      {
        icon: 'file-text',
        iconClass: 'ds-tech-icon-pink',
        title: 'Papelería',
        description:
          'Tarjetas de presentación, invitaciones para cumpleaños y ocasiones especiales, menús, flyers, carteles, pegatinas y otros materiales de papelería personalizada.',
      },
    ],

    highlightTitle: 'Equipamiento de última generación',
    highlightText1:
      'Apostamos por herramientas modernas y procesos de trabajo cuidados para ofrecer resultados consistentes y de alta calidad.',
    highlightText2:
      'Te ayudamos a elegir la técnica más adecuada según el producto, la cantidad y el tipo de personalización que necesites.',

    stats: {
      satisfaction: 'Satisfacción',
      response: 'Respuesta rápida',
      projects: 'Proyectos',
      techniques: 'Técnicas',
    },
  };

  private readonly destroy$ = new Subject<void>();

  private readonly accountService = inject(AccountService);
  private readonly router = inject(Router);
  private readonly publicCatalogService = inject(PublicCatalogService);

  ngOnInit(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe(account => this.account.set(account));

    this.loadFeaturedProducts();
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  loadFeaturedProducts(): void {
    this.publicCatalogService.getProductos().subscribe(productos => {
      this.featuredProducts = productos.filter(producto => producto.destacado).slice(0, 3);
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
