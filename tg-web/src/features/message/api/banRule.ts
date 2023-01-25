import { axios } from '@/app/lib/api/axios';
import { BanRule } from '../types';

export const listBanRules = (): Promise<BanRule[]> => {
  return axios.get(`/banRules`);
};
export const banPhoto = (uniqueId: string): Promise<void> => {
  return axios.post(`/banRules`, { property: 'fileUniqueId', rule: uniqueId });
};

export const banKeyword = (keyword: string): Promise<void> => {
  return axios.post(`/banRules`, { property: 'caption', rule: keyword });
};
