import { MessageType } from '@/app/features/message/types';
import { useAppSelector } from '@/app/hooks';
import { formatDate } from '@/app/utils';
import { PhotoSlice, VideoSlice } from '@/features/media';
import { ConfigSlice } from '@/features/misc';
import clsx from 'clsx';
import { selectMessageByUid } from '../stores/message.slice';
import { VideoFileInfo } from './VideoFileInfo';

export const MessageInfo = ({ messageUid }: { messageUid: number }) => {
  const message = useAppSelector((state) => selectMessageByUid(state, messageUid));
  let content = undefined;
  if (message?.type === MessageType.Photo) {
    content = (
      <>
        <PhotoInfo messageUid={messageUid} />
        <Time date={message.date}></Time>
      </>
    );
  } else if (message?.type === MessageType.Video) {
    content = (
      <>
        <VideoInfo2 messageUid={messageUid} />
        <PhotoInfo messageUid={messageUid} />
        <Time date={message.date}></Time>
      </>
    );
  } else {
    content = <div>{`${message?.type} content is not supported yet`}</div>;
  }

  return <div className="flex flex-col gap-2 p-1 text-sm">{content}</div>;
};

const PhotoInfo = ({ messageUid }: { messageUid: number }) => {
  const message = useAppSelector((state) => selectMessageByUid(state, messageUid));
  const photo = useAppSelector((state) => {
    if (message && message.photo) {
      return PhotoSlice.selectPhotoByUid(state, message.photo);
    } else {
      return undefined;
    }
  });
  if (message && photo) {
    return <Caption caption={photo.caption}></Caption>;
  } else {
    return <></>;
  }
};

const VideoInfo2 = ({ messageUid }: { messageUid: number }) => {
  const message = useAppSelector((state) => selectMessageByUid(state, messageUid));
  // const photo = useAppSelector((state) => {
  //   if (message && message.photo) {
  //     return PhotoSlice.selectPhotoByUid(state, message.photo);
  //   } else {
  //     return undefined;
  //   }
  // });
  // const video = useAppSelector((state) => {
  //   if (message && message.video) {
  //     return VideoSlice.selectVideoByUid(state, message.video);
  //   } else {
  //     return undefined;
  //   }
  // });
  // let fileInfo = undefined;
  const video = useAppSelector((state) => {
    if (message?.video) {
      return VideoSlice.selectVideoByUid(state, message.video);
    }
    return undefined;
  });
  if (video) {
    return <VideoFileInfo videoUid={video.uid}></VideoFileInfo>;
  }
  return <></>;
  // if (message && photo) {
  //   return <div>{fileInfo}</div>;
  // } else {
  //   return <></>;
  // }
};

const Caption = ({ caption }: { caption: string }) => {
  const nsfw = useAppSelector(ConfigSlice.selectIsNsfw);
  return (
    <div className={clsx('line-clamp-3', { 'blur-sm': nsfw })} title={caption}>
      {caption}
    </div>
  );
};

const Time = ({ date }: { date: number }) => {
  return <div className="text-right text-xs text-gray-700">{formatDate(new Date(date))}</div>;
};
