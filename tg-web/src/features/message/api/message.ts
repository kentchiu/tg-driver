import { PagingData } from '@/app/lib/api';
import { axios } from '@/app/lib/api/axios';
import { Message } from '../types';
export type AuthStage = 'PHONE_NUMBER' | 'AUTHENTICATED' | 'AUTH_CODE' | undefined;

export const listMessages = (params: URLSearchParams): Promise<PagingData<Message>> => {
  return axios.get(`/messages?${params.toString()}`, {
    transformResponse: (data) => {
      return JSON.parse(data, (key, value) => {
        if (key === 'date') {
          return new Date(value).getTime();
        } else {
          return value;
        }
      });
    }
  });
};

export const listMessageVideos = (): Promise<PagingData<Message>> => {
  return axios.get(`/message/videos?pageSize=5000`);
};