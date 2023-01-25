import { schema } from 'normalizr';

const videoEntity = new schema.Entity(
  'videos',
  {
    file: new schema.Entity('videoFiles', undefined, { idAttribute: 'uid' })
  },
  { idAttribute: 'uid' }
);

const photoEntity = new schema.Entity(
  'photos',
  {
    file: new schema.Entity('photoFiles', undefined, { idAttribute: 'uid' })
  },
  { idAttribute: 'uid' }
);

const chatEntity = new schema.Entity(
  'chats',
  {
    profile: new schema.Entity('profileFiles', undefined, { idAttribute: 'uid' })
  },
  { idAttribute: 'uid' }
);

export { videoEntity, photoEntity, chatEntity };
