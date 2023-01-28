import { useAppSelector } from '@/app/hooks';
import { formatDate } from '@/app/utils';
import { Avatar, ChatSlice } from '@/features/chat';
import { useVideoFile, VideoFileInfo } from '@/features/message';
import { ConfigSlice } from '@/features/misc';
import clsx from 'clsx';

type LineClamp = 1 | 2 | 3 | 4 | 5 | undefined;
export const VideoInfo = ({
  videoFileUid,
  stretch = true,
  lineClamp = undefined
}: {
  videoFileUid: number;
  stretch?: boolean;
  lineClamp?: LineClamp;
}) => {
  const { videos, videoFile } = useVideoFile(videoFileUid);

  return (
    <div className={clsx('flex flex-col gap-2 p-1 text-sm', { 'h-full': stretch })}>
      <VideoFileInfo videoUid={videos[0]?.uid}></VideoFileInfo>
      <div className={clsx('flex flex-grow flex-col')}>
        <Captions videoFileUid={videoFileUid} lineClamp={lineClamp}></Captions>
      </div>

      <div className="flex  items-center justify-between">
        <Avatars videoFileUid={videoFileUid}></Avatars>
        {videoFile && <Time date={videoFile.lastModified}></Time>}
      </div>
    </div>
  );
};

const Captions = ({ videoFileUid, lineClamp }: { videoFileUid: number; lineClamp?: LineClamp }) => {
  let line: string;
  switch (lineClamp) {
    case undefined:
      line = '';
      break;
    case 1:
      line = 'line-clamp-1';
      break;
    case 2:
      line = 'line-clamp-2';
      break;
    case 3:
      line = 'line-clamp-3';
      break;
    case 4:
      line = 'line-clamp-4';
      break;
    case 5:
      line = 'line-clamp-5';
      break;
    default:
      line = '';
  }
  const nsfw = useAppSelector(ConfigSlice.selectIsNsfw);
  const { videos, photos } = useVideoFile(videoFileUid);
  const videoCaptions = uniqByReduce(videos.map((val) => val.caption));
  const photoCaptions = uniqByReduce(photos.map((val) => val.caption));
  const captions = uniqByReduce(videoCaptions.concat(photoCaptions)).map((val, idx) => (
    <div className={clsx(line, { 'blur-sm': nsfw })} key={idx}>
      {val}
    </div>
  ));
  return <>{captions}</>;
};

const Avatars = ({ videoFileUid }: { videoFileUid: number }) => {
  const { messages } = useVideoFile(videoFileUid);
  const chatEntities = useAppSelector(ChatSlice.selectChatEntities);
  const avatars = messages.map((message) => {
    const chat = chatEntities[message.chat];
    return (
      <div key={`${message.uid}`} title={chat?.name}>
        <Avatar chatUid={message.chat} size="xs"></Avatar>
      </div>
    );
  });
  return (
    <dl>
      <dt className="flex justify-end -space-x-1.5 ">{avatars}</dt>
    </dl>
  );
};

const Time = ({ date }: { date: number }) => {
  return <div className="text-right text-xs text-gray-700">{formatDate(new Date(date))}</div>;
};

function uniqByReduce<T>(array: T[]): T[] {
  return array.reduce((acc: T[], cur: T) => {
    if (!acc.includes(cur)) {
      acc.push(cur);
    }
    return acc;
  }, []);
}
