/* eslint-disable no-restricted-imports */
import { Chat, CHAT_ID_ALL } from '@/features/chat';
import { selectAllChats } from '@/features/chat/stores/chat.slice';
import { selectCurrentChatUid } from '@/features/chat/stores/chatUI.slice';
import { FileSlice, PhotoSlice } from '@/features/media';
import { selectAllFiles } from '@/features/media/stores/file.slice';
import { selectAllVideos } from '@/features/media/stores/video.slice';
import { MessageSlice } from '@/features/message';
import { selectAllMessages } from '@/features/message/stores/message.slice';
import { createSelector } from 'reselect';
import { AppState } from '.';
import { Config } from '../config';

export const selectSummaryChat = createSelector(selectAllChats, (chats) => {
  const readCount = chats.map((chat) => chat.readCount).reduce((prev, current) => prev + current, 0);
  const unreadCount = chats.map((chat) => chat.unreadCount).reduce((prev, current) => prev + current, 0);
  const chatAll: Chat = {
    chatId: CHAT_ID_ALL,
    name: 'ALL',
    uid: 0,
    disabled: false,
    readCount,
    unreadCount,
    profile: 0
  };
  return chatAll;
});

export const selectCurrentChat = createSelector(
  selectAllChats,
  selectSummaryChat,
  selectCurrentChatUid,
  (chats, summary, currentUid): Chat | undefined => {
    if (currentUid === CHAT_ID_ALL) {
      return summary;
    } else {
      return chats.find((chat) => chat.uid === currentUid);
    }
  }
);

export const selectTopMessages = (state: AppState) => {
  const all = selectAllMessages(state);
  const chatUid = selectCurrentChatUid(state);
  const messages = all.filter((msg) => msg.chat === chatUid);
  const results = messages.filter((val) => !val.readAt).slice(0, Config.MESSAGES_PER_PAGE);
  return results;
};

export const selectCurrentMessage = createSelector(
  [(state: AppState) => state.messageUi.currentMessageUid, (state: AppState) => state],
  (uid, state) => {
    const message = MessageSlice.selectMessageByUid(state, uid);
    return message;
  }
);

export const selectPhotoFileByMessageUid = (state: AppState, messageUid: number) => {
  const msg = MessageSlice.selectMessageByUid(state, messageUid);
  if (msg && msg.photo) {
    const photo = PhotoSlice.selectPhotoByUid(state, msg.photo);
    if (photo && photo.file) {
      return FileSlice.selectFileByUid(state, photo.file);
    }
  }
  return undefined;
};

const selectAllVideoFiles = createSelector([(state) => state, selectAllFiles], (state, files) => {
  const videoFiles = files.filter((val) => val.localFilePath.endsWith('.mp4')).filter((val) => val.exist);
  return videoFiles;
});

export type VideoFile = {
  uid: number;
  videos: { uid: number; messages: number[] }[];
};

export const selectVideoFiles = createSelector(
  [(state) => state, selectAllVideoFiles, selectAllVideos, selectAllMessages],
  (state, files, videos, messages) => {
    if (files.length === 0) {
      return [];
    }
    const results = files.map((file) => {
      const vs = videos
        .filter((video) => video.file === file.uid)
        .map((video) => {
          const messageUids = messages.filter((msg) => msg.video === video.uid).map((val) => val.uid);
          return { uid: video.uid, messages: messageUids };
        });
      const result: VideoFile = { uid: file.uid, videos: vs };
      return result;
    });
    return results;
  }
);

export const selectVideoFileByUid = (state: AppState, fileUid: number) => {
  return selectVideoFiles(state).find((file) => file.uid === fileUid);
};
