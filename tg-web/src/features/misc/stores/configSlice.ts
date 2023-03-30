import { AppState } from '@/app/stores/store';
import storage from '@/app/utils/storage';
import { createSlice, PayloadAction } from '@reduxjs/toolkit';

export interface ConfigState {
  nsfw: boolean;
  debug: boolean;
  pageSize: number;
}

const initialState: ConfigState = {
  nsfw: false,
  debug: false,
  pageSize: 100
};

// ********** thunks *********

// ********** chatSlice *********

export const slice = createSlice({
  name: 'config',
  initialState,
  reducers: {
    isNsfw: (state) => {
      state.nsfw = storage.getNsfw();
    },
    setNsfw: (state, action: PayloadAction<boolean>) => {
      state.nsfw = action.payload;
      storage.setNsfw(state.nsfw);
    },
    debug: (state) => {
      state.debug = storage.getDebug();
    },
    setDebug: (state, action: PayloadAction<boolean>) => {
      state.debug = action.payload;
      storage.setDebug(state.debug);
    },
    pageSize: (state) => {
      state.pageSize = storage.getPageSize();
    },
    setPageSize: (state, action: PayloadAction<number>) => {
      state.pageSize = action.payload;
      storage.setPageSize(state.pageSize);
    }
  }
});

// ********** selectors **********

export const selectIsNsfw = (state: AppState) => state.configs.nsfw;
export const selectDebug = (state: AppState) => state.configs.debug;
export const selectPageSize = (state: AppState) => state.configs.pageSize;

// eslint-disable-next-line no-empty-pattern
export const { setNsfw, isNsfw, setDebug, debug, pageSize, setPageSize } = slice.actions;
const reducer = slice.reducer;
export { reducer };
