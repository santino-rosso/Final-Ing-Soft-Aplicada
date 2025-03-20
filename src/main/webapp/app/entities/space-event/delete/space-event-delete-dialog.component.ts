import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ISpaceEvent } from '../space-event.model';
import { SpaceEventService } from '../service/space-event.service';

@Component({
  templateUrl: './space-event-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class SpaceEventDeleteDialogComponent {
  spaceEvent?: ISpaceEvent;

  protected spaceEventService = inject(SpaceEventService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.spaceEventService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
