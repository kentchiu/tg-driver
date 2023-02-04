import { Config } from '@/app/config';
import { useAppSelector } from '@/app/hooks';
import { MessageHooks } from '@/features/message';
import { ConfigSlice } from '@/features/misc';
import React from 'react';

export const usePlayerSource = (videoFileUid: number | undefined) => {
  const { videoFile, messages, videos, photos, photoFiles } = MessageHooks.useVideoFile(videoFileUid);
  const [thumbnailUrl, setThumbnailUrl] = React.useState('3.jpg');
  const [videoSrc, setVideoSrc] = React.useState<string | undefined>(undefined);

  const nsfw = useAppSelector(ConfigSlice.selectIsNsfw);

  React.useEffect(() => {
    let url;
    if (nsfw || photoFiles.length == 0) {
      url = '3.jpg';
    } else {
      url = `${Config.API_ENDPOINT}/${photoFiles[0].localFilePath}`.replaceAll('\\', '/');
    }

    setThumbnailUrl(url);
  }, [nsfw, photoFiles.length]);

  React.useEffect(() => {
    let url = `${Config.API_ENDPOINT}/${videoFile?.localFilePath?.replace('#', '%23').replaceAll('\\', '/')}`;
    if (nsfw) {
      url = 'https://www.youtube.com/watch?v=dQw4w9WgXcQ'; //  Never Gonna Give You Up
    }
    if (videoFile?.localFilePath) {
      setVideoSrc(url);
    }
  }, [videoFile?.uid, nsfw]);
  return { poster: thumbnailUrl, src: videoSrc };
};
