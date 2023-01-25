import { Image } from '@/app/components';
import { useAppSelector } from '@/app/hooks';
import { FileSlice } from '@/features/media';
import clsx from 'clsx';
import { useState } from 'react';
import { selectChatByUid } from '../stores/chat.slice';

const sizes = {
  xs: 'h-4 w-4',
  sm: 'h-8 w-8',
  md: 'h-12 w-12',
  lg: 'h-16 w-16',
  xl: 'h-24 w-24'
};

export const Avatar = ({ chatUid, size = 'md' }: { chatUid: number; size?: keyof typeof sizes }) => {
  const [missing, setMissing] = useState(false);
  const chat = useAppSelector((state) => selectChatByUid(state, chatUid));
  const profile = useAppSelector((state) => FileSlice.selectFileByUid(state, chat?.profile ?? 0));

  if (missing) {
    return <TextAvatar text={chat?.name ?? ''} size={size} />;
  } else {
    return (
      <Image
        className={clsx('inline-block rounded-full', sizes[size])}
        file={profile}
        onMissingFile={() => {
          setMissing(true);
        }}
      />
    );
  }
};

const TextAvatar = ({ text, size = 'md' }: { text: string; size?: keyof typeof sizes }) => {
  const label = text.at(0);
  return (
    <>
      <div
        className={clsx(
          'flex items-center justify-center rounded-full bg-gray-100 text-2xl  font-bold text-gray-700',
          sizes[size]
        )}
      >
        {label}
      </div>
    </>
  );
};
