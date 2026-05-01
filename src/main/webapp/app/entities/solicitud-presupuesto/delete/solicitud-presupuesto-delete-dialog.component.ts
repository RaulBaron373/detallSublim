import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ISolicitudPresupuesto } from '../solicitud-presupuesto.model';
import { SolicitudPresupuestoService } from '../service/solicitud-presupuesto.service';

@Component({
  templateUrl: './solicitud-presupuesto-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class SolicitudPresupuestoDeleteDialogComponent {
  solicitudPresupuesto?: ISolicitudPresupuesto;

  protected solicitudPresupuestoService = inject(SolicitudPresupuestoService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.solicitudPresupuestoService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
