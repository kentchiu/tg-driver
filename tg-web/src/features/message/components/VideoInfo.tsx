import { useAppSelector } from '@/app/hooks';
import { VideoSlice } from '@/features/media';
import { DocumentIcon } from '@heroicons/react/24/solid';

export const VideoInfo = ({ videoUid }: { videoUid: number }) => {
  const video = useAppSelector((state) => VideoSlice.selectVideoByUid(state, videoUid));
  if (!video) {
    return <></>;
  }
  return (
    <div className="flex justify-between gap-2 text-xs text-gray-500">
      <div className="flex-shrink-0">
        <DocumentIcon className="h-3 w-3 text-sky-500"></DocumentIcon>
      </div>
      <div className="flex-shrink flex-grow overflow-hidden whitespace-nowrap" title={video.fileName}>
        {video.fileName}
      </div>
      <div className="flex-shrink-0 text-sky-500">{video.fileSize && Math.round(video.fileSize / 1024 / 1024)} M</div>
    </div>
  );
};
