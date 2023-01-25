import { useAppDispatch, useAppSelector } from '@/app/hooks';
import { Selectors } from '@/app/stores';
import { MessageUiSlice } from '@/features/message';
import { ChevronDownIcon, ChevronUpIcon, TrashIcon } from '@heroicons/react/24/solid';
import React from 'react';
import { deleteVideoFile } from '../stores/file.slice';

export const Toolbar = () => {
  const dispatch = useAppDispatch();
  const videoFileUid = useAppSelector((state) => state.messageUi.currentVideoFileUid);
  const videoFileUids: number[] = useAppSelector(Selectors.selectVideoFiles).map((val) => val.uid);

  const handleDeleteVideo = () => {
    const nextVideoFileUid = getNextVideoFileUid(videoFileUid, videoFileUids);
    dispatch(deleteVideoFile({ uid: videoFileUid, nextUid: nextVideoFileUid }));
  };

  const handlePrevious = () => {
    dispatch(MessageUiSlice.previousVideoFileUid({ currentUid: videoFileUid, uids: videoFileUids }));
  };

  const handleNext = () => {
    dispatch(MessageUiSlice.nextVideoFileUid({ currentUid: videoFileUid, uids: videoFileUids }));
  };

  // hotkeys
  React.useEffect(() => {
    const keydownHandler = (event: KeyboardEvent) => {
      switch (event.code) {
        case 'PageUp':
          handlePrevious();
          event.preventDefault();
          break;
        case 'PageDown':
          handleNext();
          event.preventDefault();
          break;
        case 'Delete':
        case 'Backspace':
          handleDeleteVideo();
          event.preventDefault();
          break;
        default:
          break;
      }
    };
    document.addEventListener('keydown', keydownHandler);
    return () => document.removeEventListener('keydown', keydownHandler);
  }, [videoFileUid]);

  return (
    <>
      <div className="flex justify-between bg-gray-800 p-2">
        <ChevronUpIcon
          className="h-6 w-6 cursor-pointer  text-gray-400 hover:text-gray-50"
          onClick={handlePrevious}
        ></ChevronUpIcon>
        <div>
          {videoFileUids.indexOf(videoFileUid) + 1}/{videoFileUids.length}
        </div>
        <ChevronDownIcon
          className="h-6 w-6 cursor-pointer text-gray-400 hover:text-gray-50"
          onClick={handleNext}
        ></ChevronDownIcon>
        <TrashIcon
          className="h-6 w-6 cursor-pointer  text-gray-400 hover:text-gray-50"
          onClick={handleDeleteVideo}
        ></TrashIcon>
      </div>
    </>
  );
};

/**
 * Get next message video uid.
 * If has no suitable next uid will return last uid or return current uid.
 * @param currentMessageVideoUid
 * @returns
 */
const getNextVideoFileUid = (currentMessageVideoUid: number, uids: number[]) => {
  const idx = uids.indexOf(currentMessageVideoUid);
  const len = uids.length;
  const NOT_FOUND = idx === -1;
  const tail = uids[len - 1];
  if (NOT_FOUND || len === 0) {
    return currentMessageVideoUid;
  }
  if (NOT_FOUND) {
    return tail;
  } else if (len == idx + 1) {
    return uids[idx - 1];
  } else if (len > idx + 1) {
    return uids[idx + 1];
  } else {
    return tail;
  }
};
