import { Component, OnDestroy, OnInit, inject, signal } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { TranslateService } from '@ngx-translate/core';
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

  currentLang = 'es';

  content: any = {
    es: {
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
          icon: '≋',
          iconClass: 'ds-tech-icon-pink',
          title: 'Sublimación',
          description: 'Ideal para tazas, bidones, camisetas técnicas, puzzles y cientos de artículos con impresión a todo color.',
        },
        {
          icon: '▣',
          iconClass: 'ds-tech-icon-cyan',
          title: 'Impresión DTF',
          description: 'Perfecta para prendas textiles de color con diseños detallados y colores vivos.',
        },
        {
          icon: '▦',
          iconClass: 'ds-tech-icon-yellow',
          title: 'Serigrafía',
          description: 'Excelente opción para pedidos grandes y ropa laboral.',
        },
        {
          icon: '✦',
          iconClass: 'ds-tech-icon-pink-soft',
          title: 'DTF UV',
          description: 'Transferencia de diseños en superficies como vidrio, madera, metal, plástico y muchos más.',
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
    },

    en: {
      badge: 'Premium Personalization',
      heroTitle: 'Customize',
      heroHighlight: 'anything you want',
      heroDescription:
        'We transform ordinary products into unique pieces with professional finishes for individuals, events and companies.',

      quoteButton: 'Request a quote',
      catalogButton: 'View products',

      technologiesTitle: 'Our Technologies',
      technologiesSubtitle: 'We use advanced techniques to achieve durable and professional finishes.',

      technologies: [
        {
          icon: '✦',
          iconClass: 'ds-tech-icon-pink',
          title: 'Sublimation',
          description: 'Ideal for mugs, T-shirts and products with full-color printing.',
        },
        {
          icon: '▣',
          iconClass: 'ds-tech-icon-cyan',
          title: 'DTG Printing',
          description: 'Perfect for textile garments with detailed designs.',
        },
        {
          icon: '◌',
          iconClass: 'ds-tech-icon-yellow',
          title: 'Screen Printing',
          description: 'Excellent option for large orders and workwear.',
        },
        {
          icon: '⚡',
          iconClass: 'ds-tech-icon-pink-soft',
          title: 'Laser',
          description: 'Precision engraving and cutting for professional finishes.',
        },
      ],
      highlightTitle: 'State-of-the-art equipment',
      highlightText1: 'We use modern tools and carefully designed processes to deliver consistent, high-quality results.',
      highlightText2:
        'We help you choose the most suitable technique according to the product, quantity and type of customization you need.',
      stats: {
        satisfaction: 'Satisfaction',
        response: 'Fast response',
        projects: 'Projects',
        techniques: 'Techniques',
      },
    },

    ca: {
      badge: 'Personalització Premium',
      heroTitle: 'Personalitza',
      heroHighlight: 'el que vulguis',
      heroDescription: 'Transformem productes ordinaris en peces úniques amb acabats professionals.',

      quoteButton: 'Sol·licita pressupost',
      catalogButton: 'Veure productes',

      technologiesTitle: 'Les nostres tecnologies',
      technologiesSubtitle: 'Utilitzem tècniques avançades per aconseguir acabats professionals.',

      technologies: [
        {
          icon: '✦',
          iconClass: 'ds-tech-icon-pink',
          title: 'Sublimació',
          description: 'Ideal per tasses, samarretes i productes amb impressió a tot color.',
        },
        {
          icon: '▣',
          iconClass: 'ds-tech-icon-cyan',
          title: 'Impressió DTG',
          description: 'Perfecta per peces tèxtils amb dissenys detallats.',
        },
        {
          icon: '◌',
          iconClass: 'ds-tech-icon-yellow',
          title: 'Serigrafia',
          description: 'Excel·lent opció per grans comandes i roba laboral.',
        },
        {
          icon: '⚡',
          iconClass: 'ds-tech-icon-pink-soft',
          title: 'Làser',
          description: 'Gravat i tall de precisió amb acabats professionals.',
        },
      ],
      highlightTitle: 'Equipament de ultima generacio',
      highlightText1: 'Fem servir eines modernes i processos cuidats per oferir resultats consistents i de qualitat.',
      highlightText2:
        'T ajudem a triar la tecnica mes adequada segons el producte, la quantitat i el tipus de personalitzacio que necessitis.',
      stats: {
        satisfaction: 'Satisfaccio',
        response: 'Resposta rapida',
        projects: 'Projectes',
        techniques: 'Tecniques',
      },
    },
  };

  private readonly destroy$ = new Subject<void>();

  private readonly accountService = inject(AccountService);
  private readonly router = inject(Router);
  private readonly translateService = inject(TranslateService);
  private readonly publicCatalogService = inject(PublicCatalogService);

  ngOnInit(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe(account => this.account.set(account));

    this.currentLang = this.translateService.currentLang || 'es';

    this.translateService.onLangChange.pipe(takeUntil(this.destroy$)).subscribe(event => {
      this.currentLang = event.lang;
    });
    this.loadFeaturedProducts();
  }

  get t(): any {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-return
    return this.content[this.currentLang] ?? this.content.es;
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadFeaturedProducts(): void {
    this.publicCatalogService.getProductos().subscribe(productos => {
      this.featuredProducts = productos.filter(producto => producto.destacado).slice(0, 3);
    });
  }
}
