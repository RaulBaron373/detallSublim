import { Component, inject } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'jhi-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss'],
})
export default class FooterComponent {
  private readonly translateService = inject(TranslateService);

  get currentLang(): string {
    return this.translateService.currentLang || 'es';
  }

  // eslint-disable-next-line @typescript-eslint/member-ordering
  content: any = {
    es: {
      tagline: 'Personalización de calidad para tus productos',
      copyright: '© 2026 Detall Sublim. Todos los derechos reservados.',
    },

    en: {
      tagline: 'Quality customization for your products',
      copyright: '© 2026 Detall Sublim. All rights reserved.',
    },

    ca: {
      tagline: 'Personalitzacio de qualitat per als teus productes',
      copyright: '© 2026 Detall Sublim. Tots els drets reservats.',
    },
  };

  get t(): any {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-return
    return this.content[this.currentLang] ?? this.content.es;
  }
}
