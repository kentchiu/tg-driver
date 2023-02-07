import { File } from '@/app/features/media/types';
import { MessageSlice } from '@/app/features/message/stores';
import { AppState } from '@/app/stores/store';
import { ChatSlice } from '@/features/chat';
import { createAsyncThunk, createEntityAdapter, createSlice } from '@reduxjs/toolkit';
import { FileApi } from '../api';

const fileAdapter = createEntityAdapter<File>({
  selectId: (file: File) => file.uid,
  sortComparer: (a, b) => b.uid - a.uid
});

// ********** thunk **********

export const deleteVideoFile = createAsyncThunk('files/deleteFile', async (arg: { uid: number; nextUid: number }) => {
  const data = await FileApi.deleteFile(arg.uid);
  return data;
});
// ********** fileSlice *********

const slice = createSlice({
  name: 'files',
  initialState: fileAdapter.getInitialState(),
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(MessageSlice.fetchUnreadMessageOfChat.fulfilled, (state, action) => {
        if (action.payload.entities.photoFiles) {
          fileAdapter.upsertMany(state, action.payload.entities.photoFiles);
        }
        if (action.payload.entities.videoFiles) {
          fileAdapter.upsertMany(state, action.payload.entities.videoFiles);
        }
      })
      .addCase(MessageSlice.fetchMessageByUids.fulfilled, (state, action) => {
        if (action.payload.entities.photoFiles) {
          fileAdapter.upsertMany(state, action.payload.entities.photoFiles);
        }
        if (action.payload.entities.videoFiles) {
          fileAdapter.upsertMany(state, action.payload.entities.videoFiles);
        }
      })
      .addCase(MessageSlice.fetchVideoMessages.fulfilled, (state, action) => {
        if (action.payload.entities.photoFiles) {
          fileAdapter.upsertMany(state, action.payload.entities.photoFiles);
        }
        if (action.payload.entities.videoFiles) {
          fileAdapter.upsertMany(state, action.payload.entities.videoFiles);
        }
      })
      .addCase(ChatSlice.fetchChats.fulfilled, (state, action) => {
        if (action.payload.entities.profileFiles) {
          fileAdapter.upsertMany(state, action.payload.entities.profileFiles);
        }
      })
      .addCase(deleteVideoFile.fulfilled, (state, action) => {
        fileAdapter.removeOne(state, action.meta.arg.uid);
      })
      .addCase(MessageSlice.fixBrokenImage.fulfilled, (state, action) => {
        const found = state.ids
          .map((id) => state.entities[id])
          .find((entity) => entity?.fileUniqueId === action.payload.fileUniqueId);

        if (found) {
          fileAdapter.updateOne(state, {
            id: found.uid,
            changes: { localFilePath: action.payload.path, lastModified: new Date().getTime() }
          });
        }
      });
  }
});

// ********** selectors **********
export const {
  selectById: selectFileByUid,
  selectIds: selectFileUids,
  selectEntities: selectFileEntities,
  selectAll: selectAllFiles,
  selectTotal: selectTotalFiles
} = fileAdapter.getSelectors<AppState>((state) => state.files);

// eslint-disable-next-line no-empty-pattern
export const {} = slice.actions;
const reducer = slice.reducer;
export { reducer };
