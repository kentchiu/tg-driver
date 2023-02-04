import { Image } from '@/app/components';
import { useAppDispatch, useAppSelector } from '@/app/hooks';
import { selectPhotoFileByMessageUid } from '@/app/stores/selectors';
import { ChevronDoubleLeftIcon, ChevronDoubleRightIcon } from '@heroicons/react/24/solid';
import clsx from 'clsx';
import React from 'react';
import { toast } from 'react-hot-toast';
import { selectMessageByUid } from '../stores/message.slice';
import { setCurrentMessageUid } from '../stores/messageUI.slice';
import { Message } from '../types';

export const Popover = ({ messageUid, messageUids }: { messageUid: number; messageUids: number[] }) => {
  // const [show, setShow] = React.useState(false);
  const dispatch = useAppDispatch();
  const message = useAppSelector((state) => selectMessageByUid(state, messageUid));
  const file = useAppSelector((state) => selectPhotoFileByMessageUid(state, message?.uid ?? -1));
  const index = messageUids.findIndex((uid) => uid === message?.uid);
  const handleCopyToClipboard = (message: Message | undefined) => {
    if (message) {
      navigator.clipboard.writeText(JSON.stringify(message)).then(
        () => {
          toast.success('copied');
        },
        () => {
          toast.error('copied fail');
        }
      );
    }
  };
  const handlePrev = () => {
    if (index > 0) {
      const prevUid = messageUids[index - 1];
      dispatch(setCurrentMessageUid(prevUid));
    }
  };
  const handleNext = () => {
    if (messageUids.length > index + 1) {
      const nextUid = messageUids[index + 1];
      dispatch(setCurrentMessageUid(nextUid));
    }
  };
  React.useEffect(() => {
    const keydownHandler = (event: KeyboardEvent) => {
      if (event.code === 'ArrowRight') {
        handleNext();
      } else if (event.code === 'ArrowLeft') {
        handlePrev();
      }
    };
    document.addEventListener('keydown', keydownHandler);
    return () => document.removeEventListener('keydown', keydownHandler);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [index]);
  if (file) {
    return (
      <>
        <div
          className="absolute z-50 h-screen w-screen py-36 backdrop-blur-sm"
          onClick={() => dispatch(setCurrentMessageUid(-1))}
        >
          <div className="flex h-full w-full items-center border-y border-gray-700 opacity-100">
            <div
              className="w-30 flex h-full flex-grow items-center justify-start bg-gray-600 opacity-50 hover:opacity-75"
              onClick={(event) => {
                handlePrev(), event.stopPropagation();
              }}
            >
              <ChevronDoubleLeftIcon className={clsx('y-20 h-20 px-4', { invisible: index === 0 })} />
            </div>
            <div
              className="relative flex h-full max-w-[80%] flex-shrink-0 flex-col justify-center opacity-100"
              // onMouseEnter={() => setShow(true)}
              // onMouseLeave={() => setShow(false)}
              onClick={(event) => {
                handleCopyToClipboard(message);
                event.stopPropagation();
              }}
            >
              <Image file={file} className="h-full object-cover"></Image>
              <div className="absolute bottom-2 w-full text-center text-4xl ">{`${index + 1}/${
                messageUids.length
              }`}</div>
            </div>
            <div
              className="w-30 onClick={(event)=>{dispatch(setCurrentMessageUid(nextUid)); event.stopPropagation()} flex h-full flex-grow items-center justify-end bg-gray-600 opacity-50 hover:opacity-75"
              onClick={(event) => {
                handleNext(), event.stopPropagation();
              }}
            >
              <ChevronDoubleRightIcon
                className={clsx('y-20 h-20 px-4', { invisible: index >= messageUids.length - 1 })}
              />
            </div>
          </div>
          {/* {show && (
            <div className="absolute top-0 left-0">
              <div className="whitespace-pre-wrap break-words bg-slate-800">{JSON.stringify(message, null, 2)}</div>
            </div>
          )} */}
        </div>
      </>
    );
  } else {
    return <></>;
  }
};
