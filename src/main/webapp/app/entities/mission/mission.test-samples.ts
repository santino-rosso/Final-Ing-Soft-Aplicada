import { IMission, NewMission } from './mission.model';

export const sampleWithRequiredData: IMission = {
  id: 12380,
  name: 'avaricious',
};

export const sampleWithPartialData: IMission = {
  id: 6364,
  name: 'regarding rundown',
};

export const sampleWithFullData: IMission = {
  id: 17181,
  name: 'fen blindly',
  description: 'until weatherize',
};

export const sampleWithNewData: NewMission = {
  name: 'pinion mathematics boulevard',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
