import { axios } from '@/app/lib/api/axios';
import { Chat } from '../types';

export const listChats = (): Promise<Chat[]> => {
  return axios.get(`/chats`, {
    transformResponse: (data) => {
      const json = JSON.parse(data);
      return json.map((val: any) => {
        const chat: Chat = {
          uid: val.uid,
          chatId: val.chatId,
          disabled: val.disabled,
          name: val.name,
          profile: val.profile,
          readCount: 0,
          unreadCount: val.messageCount ?? 0
        };
        return chat;
      });
    }
  });
};

export const refreshChats = (): Promise<{ success: boolean; chatCount: number }> => {
  return axios.put(`/chats`);
};

export const markChatAsRead = (chatUid: number, lastMessageUid: number): Promise<{ affectCount: number }> => {
  return axios.patch(`messages/mark-as-read-by-chat?chatUid=${chatUid}&lastMessageUid=${lastMessageUid}`);
};
