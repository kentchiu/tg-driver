import { Image } from '@/app/components';
import { useAppDispatch, useAppSelector } from '@/app/hooks';
import { selectPhotoFileByMessageUid } from '@/app/stores/selectors';
import { MessageSlice } from '../stores';

export const MessagePhoto = ({ messageUid }: { messageUid: number }) => {
  const file = useAppSelector((state) => selectPhotoFileByMessageUid(state, messageUid));
  const dispatch = useAppDispatch();
  const handleMissingPhoto = (src: string) => {
    console.error('broken photo image:', src);
    dispatch(MessageSlice.refreshMessage({ messageUid }));
  };
  return (
    <div className="relative">
      <Image file={file} className="w-full cursor-zoom-in" autoReload={true} onMissingFile={handleMissingPhoto}></Image>
    </div>
  );
};
