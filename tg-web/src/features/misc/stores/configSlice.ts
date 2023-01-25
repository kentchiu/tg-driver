import { AppState } from '@/app/stores/store';
import storage from '@/app/utils/storage';
import { createSlice, PayloadAction } from '@reduxjs/toolkit';

export interface ConfigState {
  nsfw: boolean;
}

const initialState: ConfigState = {
  nsfw: true
};

// ********** thunks *********

// export const isNsfw = createAsyncThunk('isNsfw', () => {
//   return storage.getNsfw();
// });

// export const setNsfw = createAsyncThunk('setNsfw', (nsfw: boolean) => {
//   setNsfw(nsfw);
// });

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
    }
  }
  // extraReducers: (builder) => {
  //   builder
  //     .addCase(isNsfw.fulfilled, (state, action) => {
  //       state.nsfw = action.payload;
  //     })
  //     .addCase(setNsfw.fulfilled, (state, action) => {
  //       state.nsfw = action.payload;
  //     });
  // }
});

// ********** selectors **********

export const selectIsNsfw = (state: AppState) => state.configs.nsfw;

// eslint-disable-next-line no-empty-pattern
export const { setNsfw, isNsfw } = slice.actions;
const reducer = slice.reducer;
export { reducer };
