import dayjs from 'dayjs/esm';

import { ISpaceEvent, NewSpaceEvent } from './space-event.model';

export const sampleWithRequiredData: ISpaceEvent = {
  id: 31202,
  name: 'almost wry',
  date: dayjs('2025-03-20'),
  description: '../fake-data/blob/hipster.txt',
  photo: '../fake-data/blob/hipster.png',
  photoContentType: 'unknown',
  type: 'LAUNCH',
};

export const sampleWithPartialData: ISpaceEvent = {
  id: 8144,
  name: 'opposite',
  date: dayjs('2025-03-19'),
  description: '../fake-data/blob/hipster.txt',
  photo: '../fake-data/blob/hipster.png',
  photoContentType: 'unknown',
  type: 'LANDING',
};

export const sampleWithFullData: ISpaceEvent = {
  id: 29241,
  name: 'as',
  date: dayjs('2025-03-19'),
  description: '../fake-data/blob/hipster.txt',
  photo: '../fake-data/blob/hipster.png',
  photoContentType: 'unknown',
  type: 'LAUNCH',
};

export const sampleWithNewData: NewSpaceEvent = {
  name: 'as sailor',
  date: dayjs('2025-03-20'),
  description: '../fake-data/blob/hipster.txt',
  photo: '../fake-data/blob/hipster.png',
  photoContentType: 'unknown',
  type: 'LAUNCH',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
