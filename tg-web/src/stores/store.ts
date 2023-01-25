import { AuthSlice } from '@/features/auth';
import { ChatSlice, ChatUiSlice } from '@/features/chat';
import { FileSlice, PhotoSlice, VideoSlice } from '@/features/media';
import { BanRuleSlice, MessageSlice, MessageUiSlice } from '@/features/message';
import { ConfigSlice } from '@/features/misc';
import { Action, configureStore, ThunkAction } from '@reduxjs/toolkit';

const makeStore = () => {
  return configureStore({
    reducer: {
      auth: AuthSlice.reducer,
      chats: ChatSlice.reducer,
      chatUi: ChatUiSlice.reducer,
      files: FileSlice.reducer,
      photos: PhotoSlice.reducer,
      videos: VideoSlice.reducer,
      banRules: BanRuleSlice.reducer,
      messages: MessageSlice.reducer,
      messageUi: MessageUiSlice.reducer,
      configs: ConfigSlice.reducer
    }
  });
};

const store = makeStore();

// Infer the `AppState` and `AppDispatch` types from the store itself
export type AppState = ReturnType<typeof store.getState>;

// Inferred type: ex:  {posts: PostsState, comments: CommentsState, users: UsersState}
export type AppDispatch = typeof store.dispatch;

export type AppThunk<ReturnType = void> = ThunkAction<ReturnType, AppState, unknown, Action<string>>;

export default store;
