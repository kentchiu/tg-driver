import { useAppSelector } from '@/app/hooks';
import { Selectors } from '@/app/stores';
import { File, FileSlice, Photo, PhotoSlice, Video, VideoSlice } from '@/features/media';
import { Message, MessageSlice } from '@/features/message';

export const useVideoFile = (videoFileUid: number | undefined) => {
  return useAppSelector((state) => {
    if (!videoFileUid) {
      return { messages: [], videos: [], photos: [], photoFiles: [] };
    }

    const videoFile = Selectors.selectVideoFileByUid(state, videoFileUid);

    const vf = FileSlice.selectFileByUid(state, videoFile ? videoFile.uid : -1);

    if (!videoFile) {
      return { messages: [], videos: [], photos: [], photoFiles: [] };
    }
    const videos = videoFile.videos
      .map((val) => VideoSlice.selectVideoByUid(state, val.uid))
      .filter((val): val is Video => !!val);

    const messages = videoFile.videos
      .flatMap((val) => val.messages)
      .map((val) => MessageSlice.selectMessageByUid(state, val))
      .filter((val): val is Message => !!val);

    const photos = messages
      .map((msg) => {
        if (msg && msg.photo) {
          return PhotoSlice.selectPhotoByUid(state, msg.photo);
        } else {
          return undefined;
        }
      })
      .filter((val): val is Photo => !!val);

    const photoFiles = photos
      .map((photo) => {
        if (photo && photo.file) {
          return FileSlice.selectFileByUid(state, photo.file);
        } else {
          return undefined;
        }
      })
      .filter((val): val is File => !!val);

    return { videoFile: vf, messages, videos, photos, photoFiles };
  });
};
