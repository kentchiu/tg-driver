import { useAppDispatch, useAppSelector } from '@/app/hooks';
import { AppState, Selectors } from '@/app/stores';
import { toDuration } from '@/app/utils';
import { Avatar, ChatSlice } from '@/features/chat';
import { MainMenu } from '@/features/layout';
import { PlayerHooks, Toolbar, VideoPlayer } from '@/features/media';
import { MessageSlice, MessageUiSlice, useVideoFile } from '@/features/message';
import { ConfigSlice } from '@/features/misc';
import clsx from 'clsx';
import React from 'react';

/**
 * next video: page down
 * previous video: page up
 * volume up: arrow up
 * volume down: arrow down
 * forward: arrow right
 * back: arrow down
 * delete video: back space or delete
 * play/pause: space
 */
const PlayerPage = () => {
  const dispatch = useAppDispatch();

  React.useEffect(() => {
    dispatch(ConfigSlice.isNsfw());
    dispatch(ChatSlice.fetchChats());
    dispatch(MessageSlice.fetchVideoMessages());
  }, [dispatch]);

  const videoFiles = useAppSelector(Selectors.selectVideoFiles);
  const videoFileUid = useAppSelector((state: AppState) => state.messageUi.currentVideoFileUid);
  const playerSource = PlayerHooks.usePlayerSource(videoFileUid);

  React.useEffect(() => {
    if (videoFileUid === -1 && videoFiles.length > 0) {
      const defaultUid = videoFiles[videoFiles.length - 1].uid;
      dispatch(MessageUiSlice.setCurrentVideoFileUid(defaultUid));
    }
  });
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
            <nav className="flex h-full w-96">
              <div className="w-full flex-col overflow-y-auto bg-gray-800">
                <Toolbar></Toolbar>
                <Info videoFileUid={videoFileUid} />
              </div>
            </nav>
            <main className="mb-14 flex w-full flex-col overflow-y-auto overflow-x-hidden bg-gray-900">
              <div className="mx-auto flex h-full w-full flex-col items-center justify-center">
                {playerSource.src && <VideoPlayer src={playerSource.src} poster={playerSource.poster}></VideoPlayer>}
              </div>
            </main>
          </div>
        </div>
      </div>
    </>
  );
};

const Info = ({ videoFileUid }: { videoFileUid: number }) => {
  const { messages, videos, photos, photoFiles } = useVideoFile(videoFileUid);
  const nsfw = useAppSelector(ConfigSlice.selectIsNsfw);
  const msgs = messages.map((message) => {
    const photoInfos = photos
      .filter((photo) => photo.uid === message.photo)
      .map((p) => <div key={p.uid}>{p.caption}</div>);
    const videoInfos = videos
      .filter((video) => video.uid === message.video)
      .map((video) => {
        return (
          <>
            <div key={video.uid + 'caption'}>caption: {video.caption}</div>
            <div key={video.uid + 'duration'}>duration: {toDuration(video.duration * 1000).format('mm:ss')}</div>
            <div key={video.uid + 'fileName'}>fileName: {video.fileName}</div>
            <div key={video.uid + 'size'}>fileSize: {video.fileSize}</div>
          </>
        );
      });

    return (
      <div key={message.uid} className="flex items-center even:opacity-50">
        <Avatar chatUid={message.chat} size="xs"></Avatar>
        <div>
          <div className={clsx('flex flex-col', { 'blur-sm': nsfw })}>{photoInfos}</div>
          <div className={clsx('flex flex-col', { 'blur-sm': nsfw })}>{videoInfos}</div>
        </div>
      </div>
    );
  });
  return <>{msgs}</>;
};
export default PlayerPage;
