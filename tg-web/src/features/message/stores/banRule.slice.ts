import type { AppState } from '@/app/stores';
import { createAsyncThunk, createEntityAdapter, createSlice } from '@reduxjs/toolkit';
import { BanRuleApi } from '../api';
import { BanRule } from '../types';

const banRuleAdapter = createEntityAdapter<BanRule>({
  sortComparer: (a, b) => b.uid - a.uid
});
const initialState = banRuleAdapter.getInitialState();

// ********** thunk **********
export const listBanRules = createAsyncThunk('listBanRules', async () => {
  const result = await BanRuleApi.listBanRules();
  return result;
});

export const banPhoto = createAsyncThunk('ban/photo', async (args: { fileUniqueId: string }) => {
  return await BanRuleApi.banPhoto(args.fileUniqueId);
});

export const banMessage = createAsyncThunk('ban/keyword', async (args: { keyword: string }) => {
  return await BanRuleApi.banKeyword(args.keyword);
});

// ********** chatSlice *********

const slice = createSlice({
  name: 'banRule',
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder.addCase(listBanRules.fulfilled, (state, action) => {
      banRuleAdapter.setAll(state, action.payload);
    });
  }
});

// ********** selectors **********

export const {
  selectById: selectBanRuleByUid,
  selectIds: selectBanRuleUids,
  selectEntities: selectBanRuleEntities,
  selectAll: selectAllBanRules,
  selectTotal: selectTotalBanRules
} = banRuleAdapter.getSelectors<AppState>((state) => state.banRules);

// eslint-disable-next-line no-empty-pattern
export const {} = slice.actions;
const reducer = slice.reducer;
export { reducer };
