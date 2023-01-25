import { AppState } from '@/app/stores';
import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { CHAT_ID_ALL } from '../types';
import { markChatAsRead } from './chat.slice';

export interface UiState {
  currentChatUid: number;
}

const initialState: UiState = {
  currentChatUid: CHAT_ID_ALL
};

const slice = createSlice({
  name: 'ui',
  initialState,
  reducers: {
    setCurrentChatId: (state, action: PayloadAction<number>) => {
      state.currentChatUid = action.payload;
    }
  },
  extraReducers: (builder) => {
    builder.addCase(markChatAsRead.fulfilled, (state, action) => {
      const chatId = action.meta.arg.nextChatId;
      if (chatId) {
        state.currentChatUid = chatId;
      }
    });
  }
});

/******** selectors ***********/
export const selectCurrentChatUid = (state: AppState) => state.chatUi.currentChatUid;

export const { setCurrentChatId: setCurrentChatUid } = slice.actions;
const reducer = slice.reducer;
export { reducer };
