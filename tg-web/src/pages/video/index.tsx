import { Card, Image } from '@/app/components';
import { useAppDispatch, useAppSelector } from '@/app/hooks';
import { Selectors } from '@/app/stores';
import { ChatSlice } from '@/features/chat';
import { MainMenu } from '@/features/layout';
import { MessageHooks, MessageSlice, MessageUiSlice, VideoInfo } from '@/features/message';
import { ConfigSlice } from '@/features/misc';
import clsx from 'clsx';
import { useRouter } from 'next/router';
import React from 'react';

const MessageVideoPage = () => {
  const dispatch = useAppDispatch();

  React.useEffect(() => {
    dispatch(ConfigSlice.isNsfw());
    dispatch(ConfigSlice.debug());
    dispatch(ChatSlice.fetchChats());
    dispatch(MessageSlice.fetchVideoMessages());
  }, [dispatch]);

  return (
    <>
      <div className="flex h-screen max-h-screen flex-row bg-gray-900 text-gray-200">
        <div className="flex flex-1 flex-col overflow-hidden">
          <header className="flex items-center justify-between border-b border-gray-500 bg-gray-900 p-4">
            <div className="flex">
              <MainMenu></MainMenu>
            </div>
          </header>
          <div className="flex h-full">
            <main className="mb-14 flex w-full flex-col overflow-y-auto overflow-x-hidden bg-gray-900">
              <div className="mx-auto flex w-full px-6 py-8">
                <div className="flex h-full w-full flex-col">
                  <MessageVideoGrid></MessageVideoGrid>
                </div>
              </div>
            </main>
          </div>
        </div>
      </div>
    </>
  );
};

const MessageVideoGrid = () => {
  const videoFiles = useAppSelector(Selectors.selectVideoFiles);
  const mv = videoFiles.map((vf) => <GridItem videoFileUid={vf.uid} key={vf.uid}></GridItem>);
  return (
    <>
      {videoFiles.length === 0 && (
        <div className="m-auto flex h-96">
          <div className="text-3xl"> No video is available at this time. Please download it first.</div>
        </div>
      )}
      <div className={clsx(`grid grid-cols-6 gap-4 overflow-y-auto px-6 py-10`)}>{mv}</div>
    </>
  );
};

const GridItem = ({ videoFileUid }: { videoFileUid: number }) => {
  const dispatch = useAppDispatch();
  const router = useRouter();
  const debug = useAppSelector((state) => state.configs.debug);

  const { photos, photoFiles } = MessageHooks.useVideoFile(videoFileUid);
  const dump = MessageHooks.useVideoFile(videoFileUid);

  const photo = photos[0];
  let clx = '';
  if (photo && photo.width > photo.height) {
    clx = 'w-full';
  } else {
    clx = 'h-full';
  }
  return (
    <Card>
      <Card.Content
        onClick={(e) => {
          if (e.ctrlKey) {
            if (debug) {
              console.log(JSON.stringify(dump, undefined, 2));
            }
          } else {
            dispatch(MessageUiSlice.setCurrentVideoFileUid(videoFileUid));
            router.push(`/player`, undefined, { shallow: true });
          }
        }}
      >
        <div className="flex h-fit justify-center bg-gray-800">
          <Image file={photoFiles[0]} className={clsx(clx)}></Image>
        </div>
      </Card.Content>
      <Card.Footer>
        <VideoInfo videoFileUid={videoFileUid} lineClamp={2}></VideoInfo>
      </Card.Footer>
    </Card>
  );
};

export default MessageVideoPage;
