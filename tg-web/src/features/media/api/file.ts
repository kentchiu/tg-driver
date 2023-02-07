/* eslint-disable @typescript-eslint/no-loss-of-precision */
import { axios } from '@/app/lib/api/axios';
import { DownloadItem } from '../types';

export const addDownloadItem = (messageUid: number): Promise<any> => {
  return axios.post(`/downloads`, { messageUid });
};

export const deleteFile = (fileUid: number): Promise<{ success: boolean; path: string }> => {
  return axios.delete(`/files/${fileUid}`);
};

export const listDownloadItems = (): Promise<DownloadItem[]> => {
  return axios.get('/downloads');
};
