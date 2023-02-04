import { useAppDispatch, useAppSelector } from '@/app/hooks';
import { AppState, Selectors } from '@/app/stores';
import { ChatSlice } from '@/features/chat';
import { MainMenu } from '@/features/layout';
import { PlayerHooks, Toolbar, VideoPlayer } from '@/features/media';
import { MessageSlice, MessageUiSlice, VideoInfo } from '@/features/message';
import { ConfigSlice } from '@/features/misc';
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
    dispatch(ConfigSlice.debug());
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
                <VideoInfo videoFileUid={videoFileUid} stretch={false} />
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

export default PlayerPage;
