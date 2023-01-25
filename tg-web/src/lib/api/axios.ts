import Axios, { AxiosError } from 'axios';

import { Config } from '@/app/config';

export const axios = Axios.create({
  baseURL: Config.API_ENDPOINT
});

axios.interceptors.response.use(
  (response) => {
    if (response.status < 100 || response.status > 300) {
      return Promise.reject(response.data);
    }
    return response.data;
  },
  (error: AxiosError) => {
    if (error?.response && error?.response?.data) {
      console.error(error.response.data);
      return Promise.reject(error?.response?.data);
    }
    return Promise.reject(error);
  }
);
