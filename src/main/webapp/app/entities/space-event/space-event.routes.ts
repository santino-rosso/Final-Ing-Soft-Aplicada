import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import SpaceEventResolve from './route/space-event-routing-resolve.service';

const spaceEventRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/space-event.component').then(m => m.SpaceEventComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/space-event-detail.component').then(m => m.SpaceEventDetailComponent),
    resolve: {
      spaceEvent: SpaceEventResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/space-event-update.component').then(m => m.SpaceEventUpdateComponent),
    resolve: {
      spaceEvent: SpaceEventResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/space-event-update.component').then(m => m.SpaceEventUpdateComponent),
    resolve: {
      spaceEvent: SpaceEventResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default spaceEventRoute;
