import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { MissionService } from '../service/mission.service';
import { IMission } from '../mission.model';
import { MissionFormService } from './mission-form.service';

import { MissionUpdateComponent } from './mission-update.component';

describe('Mission Management Update Component', () => {
  let comp: MissionUpdateComponent;
  let fixture: ComponentFixture<MissionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let missionFormService: MissionFormService;
  let missionService: MissionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MissionUpdateComponent],
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
      .overrideTemplate(MissionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MissionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    missionFormService = TestBed.inject(MissionFormService);
    missionService = TestBed.inject(MissionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const mission: IMission = { id: 25543 };

      activatedRoute.data = of({ mission });
      comp.ngOnInit();

      expect(comp.mission).toEqual(mission);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMission>>();
      const mission = { id: 27989 };
      jest.spyOn(missionFormService, 'getMission').mockReturnValue(mission);
      jest.spyOn(missionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ mission });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: mission }));
      saveSubject.complete();

      // THEN
      expect(missionFormService.getMission).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(missionService.update).toHaveBeenCalledWith(expect.objectContaining(mission));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMission>>();
      const mission = { id: 27989 };
      jest.spyOn(missionFormService, 'getMission').mockReturnValue({ id: null });
      jest.spyOn(missionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ mission: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: mission }));
      saveSubject.complete();

      // THEN
      expect(missionFormService.getMission).toHaveBeenCalled();
      expect(missionService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMission>>();
      const mission = { id: 27989 };
      jest.spyOn(missionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ mission });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(missionService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
