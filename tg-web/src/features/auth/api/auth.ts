import { axios } from '@/app/lib/api/axios';
import { AuthState } from '../types';

export const authState = (): Promise<AuthState> => {
  return axios.get(`/auth`);
};

export const sendPhoneNum = (phoneNumber: string): Promise<AuthState> => {
  return axios.post(`/auth`, { phoneNumber });
};

export const sendAuthCode = (code: string): Promise<AuthState> => {
  return axios.post(`/auth`, { code });
};
