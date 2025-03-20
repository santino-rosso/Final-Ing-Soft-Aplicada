import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISpaceEvent } from '../space-event.model';
import { SpaceEventService } from '../service/space-event.service';

const spaceEventResolve = (route: ActivatedRouteSnapshot): Observable<null | ISpaceEvent> => {
  const id = route.params.id;
  if (id) {
    return inject(SpaceEventService)
      .find(id)
      .pipe(
        mergeMap((spaceEvent: HttpResponse<ISpaceEvent>) => {
          if (spaceEvent.body) {
            return of(spaceEvent.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default spaceEventResolve;
