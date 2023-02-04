import { Image } from '@/app/components';
import { useAppDispatch, useAppSelector } from '@/app/hooks';
import { selectPhotoFileByMessageUid } from '@/app/stores/selectors';
import { MessageSlice } from '@/features/message';
import { CloudArrowDownIcon } from '@heroicons/react/24/solid';
import React from 'react';
import { toast } from 'react-hot-toast';

export const MessageThumbnail = ({ messageUid }: { messageUid: number }) => {
  const button = <DownloadButton messageUid={messageUid}></DownloadButton>;
  const file = useAppSelector((state) => selectPhotoFileByMessageUid(state, messageUid));
  const handleBrokenThumbnail = (src: string) => {
    console.error('Broken Thumbnail', src);
  };
  return (
    <>
      <div className="relative">
        <Image
          file={file}
          className="w-full cursor-zoom-in"
          autoReload={true}
          onMissingFile={handleBrokenThumbnail}
        ></Image>
        {button}
      </div>
    </>
  );
};

const DownloadButton = ({ messageUid }: { messageUid: number }) => {
  const dispatch = useAppDispatch();
  const onHandleDownload = (e: React.SyntheticEvent) => {
    e.stopPropagation();
    dispatch(MessageSlice.addToDownloadQueue({ messageUid }));
  };

  return (
    <div className="absolute top-8 -right-4 -translate-x-1/2 -translate-y-1/2 transform">
      <CloudArrowDownIcon
        className="h-12 w-12 cursor-pointer  text-gray-400   hover:text-white"
        onClick={onHandleDownload}
      />
    </div>
  );
};
