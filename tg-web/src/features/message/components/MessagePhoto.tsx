import { Image } from '@/app/components';
import { useAppSelector } from '@/app/hooks';
import { selectPhotoFileByMessageUid } from '@/app/stores/selectors';
import { useFixImage } from '../hooks/useFixImage';

export const MessagePhoto = ({ messageUid }: { messageUid: number }) => {
  const file = useAppSelector((state) => selectPhotoFileByMessageUid(state, messageUid));
  const { imageFile, fix } = useFixImage(file);
  const handleMissingPhoto = (src: string) => {
    console.error('broken photo image:', src);
    fix(messageUid);
  };
  return (
    <div className="relative">
      <Image file={imageFile} className="w-full cursor-zoom-in" onMissingFile={handleMissingPhoto}></Image>
    </div>
  );
};
