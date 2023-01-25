import { Photo } from '@/app/features/media/types';
import { AppState } from '@/app/stores/store';
import { MessageSlice } from '@/features/message';
import { createEntityAdapter, createSlice } from '@reduxjs/toolkit';

const photoAdapter = createEntityAdapter<Photo>({
  selectId: (photo: Photo) => photo.uid,
  sortComparer: (a, b) => b.uid - a.uid
});

// ********** thunk **********

// ********** photoSlice *********

export const slice = createSlice({
  name: 'photos',
  initialState: photoAdapter.getInitialState(),
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(MessageSlice.fetchUnreadMessageOfChat.fulfilled, (state, action) => {
        if (action.payload.entities.photos) {
          photoAdapter.upsertMany(state, action.payload.entities.photos);
        }
      })
      .addCase(MessageSlice.fetchMessageByUids.fulfilled, (state, action) => {
        if (action.payload.entities.photos) {
          photoAdapter.upsertMany(state, action.payload.entities.photos);
        }
      })
      .addCase(MessageSlice.fetchVideoMessages.fulfilled, (state, action) => {
        if (action.payload.entities.photos) {
          photoAdapter.upsertMany(state, action.payload.entities.photos);
        }
      });
    // .addCase(MessageVideoSlice.fetchMessageVideos.fulfilled, (state, action) => {
    //   if (action.payload.entities.photos) {
    //     photoAdapter.upsertMany(state, action.payload.entities.photos);
    //   }
    // });
  }
});

// ********** selectors **********
export const {
  selectById: selectPhotoByUid,
  selectIds: selectPhotoUids,
  selectEntities: selectPhotoEntities,
  selectAll: selectAllPhotos,
  selectTotal: selectTotalPhotos
} = photoAdapter.getSelectors<AppState>((state) => state.photos);

// actions, reducer
// eslint-disable-next-line no-empty-pattern
export const {} = slice.actions;
const reducer = slice.reducer;
export { reducer };
