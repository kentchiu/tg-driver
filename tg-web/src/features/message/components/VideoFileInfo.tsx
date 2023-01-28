import { useAppSelector } from '@/app/hooks';
import { FileSlice, VideoSlice } from '@/features/media';
import { DocumentIcon } from '@heroicons/react/24/solid';

export const VideoFileInfo = ({ videoUid }: { videoUid: number }) => {
  const video = useAppSelector((state) => VideoSlice.selectVideoByUid(state, videoUid));
  const file = useAppSelector((state) => FileSlice.selectFileByUid(state, video?.file ?? -1));
  let fileName = video?.fileName;
  if (fileName === '' && file?.localFilePath) {
    fileName = file.localFilePath.substring('downloads/videos/'.length);
  }
  if (!video) {
    return <></>;
  }
  return (
    <div className="flex justify-between gap-2 text-xs text-gray-500">
      <div className="flex-shrink-0">
        <DocumentIcon className="h-3 w-3 text-sky-500"></DocumentIcon>
      </div>
      <div className="flex-shrink flex-grow overflow-hidden whitespace-nowrap" title={fileName}>
        {fileName}
      </div>
      <div className="flex-shrink-0 text-sky-500">{video.fileSize && Math.round(video.fileSize / 1024 / 1024)} M</div>
    </div>
  );
};
