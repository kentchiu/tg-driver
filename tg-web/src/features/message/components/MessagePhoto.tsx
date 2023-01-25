import { Image } from '@/app/components';
import { useAppSelector } from '@/app/hooks';
import { selectPhotoFileByMessageUid } from '@/app/stores/selectors';

export const MessagePhoto = ({ messageUid }: { messageUid: number }) => {
  const file = useAppSelector((state) => selectPhotoFileByMessageUid(state, messageUid));
  return (
    <div className="relative">
      <Image file={file} className="w-full cursor-zoom-in"></Image>
    </div>
  );
};
