import { AppState } from '@/app/stores';
import { createAsyncThunk, createEntityAdapter, createSlice } from '@reduxjs/toolkit';
import { toast } from 'react-hot-toast';
import { FileApi } from '../api';
import { DownloadItem } from '../types';

const downloadAdapter = createEntityAdapter<DownloadItem>({
  selectId: (item: DownloadItem) => item.fileId,
  sortComparer: (a, b) => b.fileId - a.fileId
});

// ********** thunk **********
export const fetchDownloadItems = createAsyncThunk('download/list', () => {
  return FileApi.listDownloadItems();
});

export const addDownloadItem = createAsyncThunk('download/add', async (args: { messageUid: number }) => {
  return await FileApi.addDownloadItem(args.messageUid);
});
// ********** fileSlice *********

const slice = createSlice({
  name: 'files',
  initialState: downloadAdapter.getInitialState(),
  reducers: {},
  extraReducers: (builder) => {
    builder.addCase(fetchDownloadItems.fulfilled, (state, action) => {
      downloadAdapter.setAll(state, action.payload);
    }),
      builder.addCase(addDownloadItem.fulfilled, (state, action) => {
        toast.success('Success');
      });
    builder.addCase(addDownloadItem.rejected, (state, action) => {
      toast.error(action.error.message ?? 'Download fail');
    });
  }
});

// ********** selectors **********
export const {
  selectById: selectDownloadItemById,
  selectIds: selectDownloadItemUids,
  selectEntities: selectDownloadItemEntities,
  selectAll: selectAllDownloadItems,
  selectTotal: selectTotalDownloadItems
} = downloadAdapter.getSelectors<AppState>((state) => state.downloads);

// eslint-disable-next-line no-empty-pattern
export const {} = slice.actions;
const reducer = slice.reducer;
export { reducer };
