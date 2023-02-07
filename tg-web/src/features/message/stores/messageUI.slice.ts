import { FileSlice } from '@/features/media';
import { createSlice, PayloadAction } from '@reduxjs/toolkit';

export interface UiState {
  currentMessageUid: number;
  currentVideoFileUid: number;
  columnCount: number;
}

const initialState: UiState = {
  currentMessageUid: -1,
  currentVideoFileUid: -1,
  columnCount: 6
};

const slice = createSlice({
  name: 'ui',
  initialState,
  reducers: {
    setCurrentMessageUid: (state, action: PayloadAction<number>) => {
      state.currentMessageUid = action.payload;
    },
    setCurrentVideoFileUid: (state, action: PayloadAction<number>) => {
      state.currentVideoFileUid = action.payload;
    },
    nextVideoFileUid: (state, action: PayloadAction<{ currentUid: number; uids: number[] }>) => {
      state.currentVideoFileUid = next(action.payload.currentUid, action.payload.uids);
    },
    previousVideoFileUid: (state, action: PayloadAction<{ currentUid: number; uids: number[] }>) => {
      state.currentVideoFileUid = previous(action.payload.currentUid, action.payload.uids);
    },
    setColumnSize: (state, action: PayloadAction<number>) => {
      state.columnCount = action.payload;
    }
  },
  extraReducers: (builder) => {
    builder.addCase(FileSlice.deleteVideoFile.fulfilled, (state, action) => {
      state.currentVideoFileUid = action.meta.arg.nextUid;
    });
  }
});

const next = (uid: number, uids: number[]) => {
  if (uids == null || uids.length === 0) {
    return uid;
  }
  const idx = uids.indexOf(uid);
  const notFound = idx === -1;
  const head = uids[0];
  const len = uids.length;
  const tail = uids[len - 1];
  if (notFound) {
    return head;
  }
  if (len > idx + 2) {
    return uids[idx + 1];
  } else {
    return tail;
  }
};

const previous = (uid: number, uids: number[]) => {
  if (uids == null || uids.length === 0) {
    return uid;
  }
  const idx = uids.indexOf(uid);
  const notFound = idx === -1;
  const head = uids[0];
  if (notFound) {
    return head;
  }
  if (idx > 1) {
    return uids[idx - 1];
  } else {
    return head;
  }
};

export const { setCurrentMessageUid, setCurrentVideoFileUid, previousVideoFileUid, nextVideoFileUid, setColumnSize } =
  slice.actions;
const reducer = slice.reducer;
export { reducer };
