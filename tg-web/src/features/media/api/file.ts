import { axios } from '@/app/lib/api/axios';

export const addToDownloadQueue = (messageUid: number): Promise<any> => {
  return axios.post(`/queue`, { messageUid });
};

export const deleteFile = (fileUid: number): Promise<{ success: boolean; path: string }> => {
  return axios.delete(`/files/${fileUid}`);
};
