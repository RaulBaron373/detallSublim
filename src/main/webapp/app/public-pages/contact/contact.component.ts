import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import dayjs from 'dayjs/esm';

import SharedModule from 'app/shared/shared.module';
import { MensajeContactoService } from 'app/entities/mensaje-contacto/service/mensaje-contacto.service';
import { NewMensajeContacto } from 'app/entities/mensaje-contacto/mensaje-contacto.model';

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

  constructor(private mensajeContactoService: MensajeContactoService) {}

  enviarMensaje(): void {
    this.successMessage = '';
    this.errorMessage = '';

    if (!this.form.nombre.trim() || !this.form.email.trim() || !this.form.asunto.trim() || !this.form.mensaje.trim()) {
      this.errorMessage = 'Debes completar todos los campos obligatorios.';

      return;
    }

    const payload: NewMensajeContacto = {
      id: null,
      ...this.form,
      fechaEnvio: dayjs(),
      atendido: false,
    };

    this.mensajeContactoService.create(payload).subscribe({
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
