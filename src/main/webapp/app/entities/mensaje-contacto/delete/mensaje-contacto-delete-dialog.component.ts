import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IMensajeContacto } from '../mensaje-contacto.model';
import { MensajeContactoService } from '../service/mensaje-contacto.service';

@Component({
  templateUrl: './mensaje-contacto-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class MensajeContactoDeleteDialogComponent {
  mensajeContacto?: IMensajeContacto;

  protected mensajeContactoService = inject(MensajeContactoService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.mensajeContactoService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
