import { Component, signal } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faWhatsapp } from '@fortawesome/free-brands-svg-icons';
import { filter } from 'rxjs';

@Component({
  selector: 'jhi-whatsapp-button',
  standalone: true,
  imports: [FontAwesomeModule],
  templateUrl: './whatsapp-button.component.html',
  styleUrl: './whatsapp-button.component.scss',
})
export class WhatsappButtonComponent {
  readonly whatsappIcon = faWhatsapp;

  readonly whatsappUrl =
    'https://wa.me/34618006543?text=Hola%2C%20vengo%20desde%20la%20web%20de%20Detall%20Sublim%20y%20me%20gustar%C3%ADa%20pedir%20informaci%C3%B3n%20sobre%20un%20producto%20personalizado.';

  readonly visible = signal(true);

  private readonly privateRoutes = [
    '/panel',
    '/login',
    '/admin',
    '/account',
    '/authority',
    '/categoria',
    '/producto',
    '/solicitud-presupuesto',
    '/mensaje-contacto',
  ];

  constructor(private readonly router: Router) {
    this.updateVisibility(this.router.url);

    this.router.events
      .pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd))
      .subscribe(event => this.updateVisibility(event.urlAfterRedirects));
  }

  private updateVisibility(url: string): void {
    const path = url.split('?')[0].split('#')[0];

    const isPrivateRoute = this.privateRoutes.some(route => path === route || path.startsWith(`${route}/`));

    this.visible.set(!isPrivateRoute);
  }
}
