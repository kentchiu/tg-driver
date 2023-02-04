import { useAppSelector } from '@/app/hooks';
import { FileSlice, PhotoSlice, VideoSlice } from '@/features/media';
import { MessageSlice } from '../stores';

export const useMessage = (messageUid: number) => {
  const message = useAppSelector((state) => {
    const NOT_EXIST_ID = -1;
    const message = MessageSlice.selectMessageByUid(state, messageUid);
    const photo = PhotoSlice.selectPhotoByUid(state, message?.photo ?? NOT_EXIST_ID);
    const video = VideoSlice.selectVideoByUid(state, message?.video ?? NOT_EXIST_ID);
    const photoFile = FileSlice.selectFileByUid(state, photo?.file ?? NOT_EXIST_ID);
    const videoFile = FileSlice.selectFileByUid(state, video?.file ?? NOT_EXIST_ID);
    return { message, photo, photoFile, video, videoFile };
  });

  return { message };
};
