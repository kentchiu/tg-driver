import { File, FileApi } from '@/features/media';
import { ConfigSlice } from '@/features/misc';
import React, { SyntheticEvent } from 'react';
import { Config } from '../config';
import { useAppSelector } from '../hooks';

export type ImageProps = {
  className?: string;
  file?: File;
  fallbackUrl?: string;
  autoReload?: boolean;
  onMissingFile?: (src: string) => void;
};

export const Image = ({ className, file, fallbackUrl = '3.jpg', autoReload = false, onMissingFile }: ImageProps) => {
  const nsfw = useAppSelector(ConfigSlice.selectIsNsfw);
  let imgSrc = fallbackUrl;
  if (file) {
    imgSrc = `${Config.API_ENDPOINT}/${file.localFilePath}`;
  }

  if (nsfw) {
    imgSrc = fallbackUrl;
    const images = ['3.jpg', '4.jpg', '5.jpg'];
    const i = Math.floor(Math.random() * images.length);
    imgSrc = images[i];
  }
  const imageRef = React.useRef<HTMLImageElement>(null);
  const [retired, setRetried] = React.useState(false);

  return (
    <>
      {
        <img
          ref={imageRef}
          src={imgSrc}
          className={className}
          onError={(event: SyntheticEvent<HTMLImageElement, Event>) => {
            console.log('broken image:', imgSrc);
            setRetried(true);
            if (file?.fileUniqueId && !retired && autoReload) {
              console.log('download iamge', file.fileUniqueId);
              FileApi.downloadPhotoFile(file.fileUniqueId)
                .then((val) => {
                  if (imageRef.current) {
                    imageRef.current.src = imgSrc;
                  }
                })
                .catch((error) => {
                  console.warn(error);
                  if (imageRef.current) {
                    imageRef.current.src = fallbackUrl;
                  }
                });
            }
            onMissingFile && onMissingFile(imgSrc);
          }}
        />
      }
    </>
  );
};
