import { Video } from '@/app/features/media/types';
import { AppState } from '@/app/stores/store';
import { MessageSlice } from '@/features/message';
import { createEntityAdapter, createSlice } from '@reduxjs/toolkit';

const videoAdapter = createEntityAdapter<Video>({
  selectId: (video: Video) => video.uid,
  sortComparer: (a, b) => b.uid - a.uid
});

// ********** thunk **********

// ********** videoSlice *********
const slice = createSlice({
  name: 'videos',
  initialState: videoAdapter.getInitialState(),
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(MessageSlice.fetchUnreadMessageOfChat.fulfilled, (state, action) => {
        if (action.payload.entities.videos) {
          videoAdapter.upsertMany(state, action.payload.entities.videos);
        }
      })
      .addCase(MessageSlice.fetchMessageByUids.fulfilled, (state, action) => {
        if (action.payload.entities.videos) {
          videoAdapter.upsertMany(state, action.payload.entities.videos);
        }
      })
      .addCase(MessageSlice.fetchVideoMessages.fulfilled, (state, action) => {
        if (action.payload.entities.videos) {
          videoAdapter.upsertMany(state, action.payload.entities.videos);
        }
      });
  }
});

// ********** selectors **********
export const {
  selectById: selectVideoByUid,
  selectIds: selectVideoUids,
  selectEntities: selectVideoEntities,
  selectAll: selectAllVideos,
  selectTotal: selectTotalVideos
} = videoAdapter.getSelectors<AppState>((state) => state.videos);

// eslint-disable-next-line no-empty-pattern
export const {} = slice.actions;
const reducer = slice.reducer;
export { reducer };
