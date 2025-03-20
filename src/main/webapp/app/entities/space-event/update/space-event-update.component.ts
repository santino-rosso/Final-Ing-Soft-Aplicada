import { Component, ElementRef, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IMission } from 'app/entities/mission/mission.model';
import { MissionService } from 'app/entities/mission/service/mission.service';
import { SpaceEventType } from 'app/entities/enumerations/space-event-type.model';
import { SpaceEventService } from '../service/space-event.service';
import { ISpaceEvent } from '../space-event.model';
import { SpaceEventFormGroup, SpaceEventFormService } from './space-event-form.service';

@Component({
  selector: 'jhi-space-event-update',
  templateUrl: './space-event-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class SpaceEventUpdateComponent implements OnInit {
  isSaving = false;
  spaceEvent: ISpaceEvent | null = null;
  spaceEventTypeValues = Object.keys(SpaceEventType);

  missionsCollection: IMission[] = [];

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected spaceEventService = inject(SpaceEventService);
  protected spaceEventFormService = inject(SpaceEventFormService);
  protected missionService = inject(MissionService);
  protected elementRef = inject(ElementRef);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: SpaceEventFormGroup = this.spaceEventFormService.createSpaceEventFormGroup();

  compareMission = (o1: IMission | null, o2: IMission | null): boolean => this.missionService.compareMission(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ spaceEvent }) => {
      this.spaceEvent = spaceEvent;
      if (spaceEvent) {
        this.updateForm(spaceEvent);
      }

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('spaceApp.error', { ...err, key: `error.file.${err.key}` })),
    });
  }

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    if (idInput && this.elementRef.nativeElement.querySelector(`#${idInput}`)) {
      this.elementRef.nativeElement.querySelector(`#${idInput}`).value = null;
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const spaceEvent = this.spaceEventFormService.getSpaceEvent(this.editForm);
    if (spaceEvent.id !== null) {
      this.subscribeToSaveResponse(this.spaceEventService.update(spaceEvent));
    } else {
      this.subscribeToSaveResponse(this.spaceEventService.create(spaceEvent));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISpaceEvent>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(spaceEvent: ISpaceEvent): void {
    this.spaceEvent = spaceEvent;
    this.spaceEventFormService.resetForm(this.editForm, spaceEvent);

    this.missionsCollection = this.missionService.addMissionToCollectionIfMissing<IMission>(this.missionsCollection, spaceEvent.mission);
  }

  protected loadRelationshipsOptions(): void {
    this.missionService
      .query({ filter: 'spaceevent-is-null' })
      .pipe(map((res: HttpResponse<IMission[]>) => res.body ?? []))
      .pipe(
        map((missions: IMission[]) => this.missionService.addMissionToCollectionIfMissing<IMission>(missions, this.spaceEvent?.mission)),
      )
      .subscribe((missions: IMission[]) => (this.missionsCollection = missions));
  }
}
