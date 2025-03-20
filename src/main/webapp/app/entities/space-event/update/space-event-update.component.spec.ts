import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IMission } from 'app/entities/mission/mission.model';
import { MissionService } from 'app/entities/mission/service/mission.service';
import { SpaceEventService } from '../service/space-event.service';
import { ISpaceEvent } from '../space-event.model';
import { SpaceEventFormService } from './space-event-form.service';

import { SpaceEventUpdateComponent } from './space-event-update.component';

describe('SpaceEvent Management Update Component', () => {
  let comp: SpaceEventUpdateComponent;
  let fixture: ComponentFixture<SpaceEventUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let spaceEventFormService: SpaceEventFormService;
  let spaceEventService: SpaceEventService;
  let missionService: MissionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [SpaceEventUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(SpaceEventUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SpaceEventUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    spaceEventFormService = TestBed.inject(SpaceEventFormService);
    spaceEventService = TestBed.inject(SpaceEventService);
    missionService = TestBed.inject(MissionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call mission query and add missing value', () => {
      const spaceEvent: ISpaceEvent = { id: 24890 };
      const mission: IMission = { id: 27989 };
      spaceEvent.mission = mission;

      const missionCollection: IMission[] = [{ id: 27989 }];
      jest.spyOn(missionService, 'query').mockReturnValue(of(new HttpResponse({ body: missionCollection })));
      const expectedCollection: IMission[] = [mission, ...missionCollection];
      jest.spyOn(missionService, 'addMissionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ spaceEvent });
      comp.ngOnInit();

      expect(missionService.query).toHaveBeenCalled();
      expect(missionService.addMissionToCollectionIfMissing).toHaveBeenCalledWith(missionCollection, mission);
      expect(comp.missionsCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const spaceEvent: ISpaceEvent = { id: 24890 };
      const mission: IMission = { id: 27989 };
      spaceEvent.mission = mission;

      activatedRoute.data = of({ spaceEvent });
      comp.ngOnInit();

      expect(comp.missionsCollection).toContainEqual(mission);
      expect(comp.spaceEvent).toEqual(spaceEvent);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISpaceEvent>>();
      const spaceEvent = { id: 10679 };
      jest.spyOn(spaceEventFormService, 'getSpaceEvent').mockReturnValue(spaceEvent);
      jest.spyOn(spaceEventService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ spaceEvent });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: spaceEvent }));
      saveSubject.complete();

      // THEN
      expect(spaceEventFormService.getSpaceEvent).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(spaceEventService.update).toHaveBeenCalledWith(expect.objectContaining(spaceEvent));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISpaceEvent>>();
      const spaceEvent = { id: 10679 };
      jest.spyOn(spaceEventFormService, 'getSpaceEvent').mockReturnValue({ id: null });
      jest.spyOn(spaceEventService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ spaceEvent: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: spaceEvent }));
      saveSubject.complete();

      // THEN
      expect(spaceEventFormService.getSpaceEvent).toHaveBeenCalled();
      expect(spaceEventService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISpaceEvent>>();
      const spaceEvent = { id: 10679 };
      jest.spyOn(spaceEventService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ spaceEvent });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(spaceEventService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareMission', () => {
      it('Should forward to missionService', () => {
        const entity = { id: 27989 };
        const entity2 = { id: 25543 };
        jest.spyOn(missionService, 'compareMission');
        comp.compareMission(entity, entity2);
        expect(missionService.compareMission).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
