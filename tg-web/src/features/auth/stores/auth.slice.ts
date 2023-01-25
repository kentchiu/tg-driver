import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import { AuthApi } from '../api';
import { AuthStage } from '../types';

// ********** thunk **********

export const fetchAuthStage = createAsyncThunk('auth', async () => {
  return await AuthApi.authState();
});

export const updatePhoneNumber = createAsyncThunk('auth/phoneNumber', async (phoneNumber: string) => {
  return await AuthApi.sendPhoneNum(phoneNumber);
});

export const updateAuthCode = createAsyncThunk('auth/authCode', async (authCode: string) => {
  return await AuthApi.sendAuthCode(authCode);
});

export interface AuthState {
  phoneNumber: string | undefined;
  stage: AuthStage;
  phoneNumberStatus: 'idle' | 'loading' | 'success' | 'failed';
  authCodeStatus: 'idle' | 'loading' | 'success' | 'failed';
  phoneNumberError?: unknown;
}

const initialState: AuthState = {
  phoneNumber: undefined,
  stage: undefined,
  phoneNumberStatus: 'idle',
  authCodeStatus: 'idle'
};

const slice = createSlice({
  name: 'auth',
  initialState: initialState,
  reducers: {},
  extraReducers(builder) {
    builder
      .addCase(fetchAuthStage.fulfilled, (state, action) => {
        state.stage = action.payload.stage;
        state.phoneNumberError = undefined;
        if (state.stage === 'AUTH_CODE') {
          state.phoneNumber = action.payload.phoneNumber;
        }
      })
      .addCase(updatePhoneNumber.pending, (state, action) => {
        state.phoneNumberStatus = 'loading';
        state.phoneNumber = action.meta.arg;
      })
      .addCase(updatePhoneNumber.fulfilled, (state, action) => {
        state.phoneNumberStatus = 'success';
        state.stage = 'AUTH_CODE';
      })
      .addCase(updatePhoneNumber.rejected, (state, action) => {
        state.phoneNumberStatus = 'failed';
        state.phoneNumberError = action.error.message;
      })
      .addCase(updateAuthCode.pending, (state, action) => {
        state.authCodeStatus = 'loading';
      })
      .addCase(updateAuthCode.fulfilled, (state, action) => {
        state.authCodeStatus = 'success';
        state.stage = 'AUTHENTICATED';
      })
      .addCase(updateAuthCode.rejected, (state, action) => {
        state.authCodeStatus = 'failed';
      });
  }
});

export const reducer = slice.reducer;
