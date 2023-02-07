import { useAppDispatch } from '@/app/hooks';
import { File } from '@/features/media';
import React from 'react';
import { MessageSlice } from '../stores';

export const useFixImage = (file: File | undefined) => {
  const dispatch = useAppDispatch();
  const [imageFile, setImageFile] = React.useState<File | undefined>(file);

  const fix = (messageUid: number) => {
    dispatch(MessageSlice.fixBrokenImage({ messageUid }))
      .unwrap()
      .then(() => {
        setImageFile((prev) => {
          if (prev) {
            return { ...prev, localFilePath: prev.localFilePath + '?retry=1' };
          }
          return prev;
        });
      });
  };

  return { imageFile, fix };
};
