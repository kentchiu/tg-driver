import { AppState } from '@/app/stores';
import { chatEntity, photoEntity, videoEntity } from '@/app/types';
import { ChatSlice } from '@/features/chat';
import { FileApi } from '@/features/media';
import { createAsyncThunk, createEntityAdapter, createSlice, Update } from '@reduxjs/toolkit';
import { normalize, schema } from 'normalizr';
import { MessageApi } from '../api';
import { Message } from '../types';

const messageAdapter = createEntityAdapter<Message>({
  selectId: (message) => message.uid,
  sortComparer: (a, b) => a.uid - b.uid
});
// ********** thunk **********

export const fetchUnreadMessageOfChat = createAsyncThunk('message/fetchMessagesOfChat', async (chatUid: number) => {
  const params = new URLSearchParams();
  params.set('chatUid', chatUid.toString());
  params.set('pageSize', '5000');
  const data = await MessageApi.listMessages(params);
  const messageSchema = new schema.Array({ chat: chatEntity, photo: photoEntity, video: videoEntity });
  const normalizeData = normalize(data.records, messageSchema);
  return { ...normalizeData, total: data.total };
});

export const fetchMessageByUids = createAsyncThunk('message/fetchMessageByUids', async (uids: number[]) => {
  const params = new URLSearchParams();
  params.set('uids', uids.toString());
  params.set('pageSize', '5000');
  const data = await MessageApi.listMessages(params);
  const messageSchema = new schema.Array({ chat: chatEntity, photo: photoEntity, video: videoEntity });
  const normalizeData = normalize(data.records, messageSchema);
  return { ...normalizeData, total: data.total };
});

export const fetchVideoMessages = createAsyncThunk('message/fetchVideoMessages', async () => {
  const data = await MessageApi.listMessageVideos();
  const messageSchema = new schema.Array({ chat: chatEntity, photo: photoEntity, video: videoEntity });
  const normalizeData = normalize(data.records, messageSchema);
  return { ...normalizeData, total: data.total };
});

export const fixBrokenImage = createAsyncThunk('message/fixBrokerImage', async (args: { messageUid: number }) => {
  return await MessageApi.fixBrokenImage(args.messageUid);
});

// ********** messageSlice *********

const slice = createSlice({
  name: 'message',
  initialState: messageAdapter.getInitialState(),
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchUnreadMessageOfChat.fulfilled, (state, action) => {
        if (action.payload.result) {
          messageAdapter.upsertMany(state, action.payload.result);
        }
      })
      .addCase(fetchMessageByUids.fulfilled, (state, action) => {
        if (action.payload.result) {
          messageAdapter.upsertMany(state, action.payload.result);
        }
      })
      .addCase(fetchVideoMessages.fulfilled, (state, action) => {
        if (action.payload.result) {
          messageAdapter.upsertMany(state, action.payload.result);
        }
      })
      .addCase(ChatSlice.markChatAsRead.fulfilled, (state, action) => {
        const now = new Date();
        const updates = action.meta.arg.messageUids.map((id) => {
          const update: Update<Message> = { id: id, changes: { readAt: now.toDateString() } };
          return update;
        });
        messageAdapter.updateMany(state, updates);
      });
  }
});

// ********* selectors ************
export const {
  selectAll: selectAllMessages,
  selectById: selectMessageByUid,
  selectIds: selectAllMessageUids,
  selectEntities: selectAllMessageEntities,
  selectTotal: selectMessageTotal
} = messageAdapter.getSelectors<AppState>((state) => state.messages);

// actions, reducer
// eslint-disable-next-line no-empty-pattern
export const {} = slice.actions;
const reducer = slice.reducer;
export { reducer };
