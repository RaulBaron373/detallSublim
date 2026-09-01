import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';

@Component({
  selector: 'jhi-contact',
  imports: [FormsModule, RouterModule, SharedModule],
  standalone: true,
  templateUrl: './contact.component.html',
  styleUrl: './contact.component.scss',
})
export class ContactComponent {
  form = {
    nombre: '',
    email: '',
    telefono: '',
    asunto: '',
    mensaje: '',
  };

  successMessage = '';
  errorMessage = '';

  constructor(private readonly http: HttpClient) {}

  enviarMensaje(): void {
    this.successMessage = '';
    this.errorMessage = '';

    if (!this.form.nombre.trim() || !this.form.email.trim() || !this.form.asunto.trim() || !this.form.mensaje.trim()) {
      this.errorMessage = 'Debes completar todos los campos obligatorios.';

      return;
    }

    const payload = {
      nombre: this.form.nombre,
      email: this.form.email,
      telefono: this.form.telefono,
      asunto: this.form.asunto,
      mensaje: this.form.mensaje,
    };

    this.http.post<unknown>('/api/public/contact', payload).subscribe({
      next: () => {
        this.successMessage = 'Mensaje enviado correctamente.';

        this.form = {
          nombre: '',
          email: '',
          telefono: '',
          asunto: '',
          mensaje: '',
        };
      },

      error: () => {
        this.errorMessage = 'Ha ocurrido un error al enviar el mensaje.';
      },
    });
  }
}
