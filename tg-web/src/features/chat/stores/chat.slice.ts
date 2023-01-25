import { Chat } from '@/app/features/chat/types';
import { AppState } from '@/app/stores/store';
import { MessageSlice } from '@/features/message';
import { createAsyncThunk, createEntityAdapter, createSlice, PayloadAction } from '@reduxjs/toolkit';
import { normalize, schema } from 'normalizr';
import * as api from '../api/chat';

const chatAdapter = createEntityAdapter<Chat>({
  selectId: (chat: Chat) => chat.uid,
  sortComparer: (a, b) => {
    const c1 = a.unreadCount + a.readCount;
    const c2 = b.unreadCount + b.readCount;
    return c2 - c1;
  }
});

// ********** thunk **********

export const fetchChats = createAsyncThunk('chat/fetchChats', async () => {
  const data = await api.listChats();
  const chatSchema = new schema.Array({
    profile: new schema.Entity('profileFiles', undefined, { idAttribute: 'uid' })
  });
  const normalizeData = normalize(data, chatSchema);
  return normalizeData;
});

export const refreshChats = createAsyncThunk('chat/refreshChats', async () => {
  const data = await api.refreshChats();
  return data;
});

// FIXME: ensure the return type
export const markChatAsRead = createAsyncThunk(
  'chat/mark-as-read',
  async (params: { chatUid: number; nextChatId?: number; messageUids: number[] }) => {
    if (params.messageUids.length > 0) {
      const lastMessageUid = params.messageUids[params.messageUids.length - 1];
      const result = await api.markChatAsRead(params.chatUid, lastMessageUid);
      return result;
    }
    return {};
  }
);

// ********** chatSlice *********

const slice = createSlice({
  name: 'chats',
  initialState: chatAdapter.getInitialState(),
  reducers: {
    updateMessageCount: (state, action: PayloadAction<{ id: number; messageCount: number }>) => {
      chatAdapter.updateOne(state, { id: action.payload.id, changes: { readCount: action.payload.messageCount } });
    }
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchChats.fulfilled, (state, action) => {
        if (action.payload.result) {
          chatAdapter.upsertMany(state, action.payload.result);
        }
      })
      .addCase(MessageSlice.fetchUnreadMessageOfChat.fulfilled, (state, action) => {
        chatAdapter.updateOne(state, { id: action.meta.arg, changes: { unreadCount: action.payload.total } });
      })
      .addCase(markChatAsRead.fulfilled, (state, action) => {
        const chatUid = action.meta.arg.chatUid;
        const chat = state.entities[chatUid];
        const len = action.meta.arg.messageUids.length;
        if (len > 0 && chat) {
          const readCount = chat.readCount + len;
          const unreadCount = chat.unreadCount - len;
          chatAdapter.updateOne(state, { id: chatUid, changes: { readCount, unreadCount } });
        }
      });
  }
});

// ********** selectors **********
export const {
  selectById: selectChatByUid,
  selectIds: selectChatUids,
  selectEntities: selectChatEntities,
  selectAll: selectAllChats,
  selectTotal: selectTotalChats
} = chatAdapter.getSelectors<AppState>((state) => state.chats);

export const { updateMessageCount } = slice.actions;
const reducer = slice.reducer;
export { reducer };
