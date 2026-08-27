import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

@Component({
  selector: 'jhi-error',
  templateUrl: './error.component.html',
  styleUrl: './error.component.scss',
  imports: [RouterLink],
})
export default class ErrorComponent implements OnInit {
  statusCode = signal('500');

  eyebrow = signal('ERROR DEL SISTEMA');

  heading = signal('Algo no ha salido como esperábamos');

  description = signal('Ha ocurrido un error inesperado. Puedes volver al inicio e intentarlo de nuevo.');

  private readonly route = inject(ActivatedRoute);

  ngOnInit(): void {
    this.route.data.subscribe(routeData => {
      this.statusCode.set(routeData['statusCode'] ?? '500');

      this.eyebrow.set(routeData['eyebrow'] ?? 'ERROR DEL SISTEMA');

      this.heading.set(routeData['heading'] ?? 'Algo no ha salido como esperábamos');

      this.description.set(routeData['description'] ?? 'Ha ocurrido un error inesperado. Puedes volver al inicio e intentarlo de nuevo.');
    });
  }
}
