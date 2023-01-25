import { useAppDispatch, useAppSelector } from '@/app/hooks';
import { Selectors } from '@/app/stores';
import { ConfigSlice } from '@/features/misc';
import clsx from 'clsx';
import React from 'react';
import { selectChatByUid, selectChatUids } from '../stores/chat.slice';
import { selectCurrentChatUid, setCurrentChatUid } from '../stores/chatUI.slice';
import { Avatar } from './Avatar';

export const ChatList = () => {
  const [sortedUids, setSortedUids] = React.useState<number[]>([]);
  const chatUids = useAppSelector(selectChatUids) as number[];
  const currentChatUid = useAppSelector(selectCurrentChatUid);

  React.useEffect(() => {
    setSortedUids(chatUids);
  }, [currentChatUid, chatUids.length]);

  return (
    <>
      <div className="sticky top-0 z-50">
        <Head></Head>
      </div>
      {sortedUids.map((chatUid) => {
        return (
          <React.Fragment key={chatUid}>
            <ListItem chatUid={chatUid} isActive={chatUid === currentChatUid}></ListItem>
          </React.Fragment>
        );
      })}
    </>
  );
};

const ListItem = ({ chatUid: chatUid, isActive = false }: { chatUid: number; isActive: boolean }) => {
  const dispatch = useAppDispatch();
  const chat = useAppSelector((state) => selectChatByUid(state, chatUid));
  const nsfw = useAppSelector(ConfigSlice.selectIsNsfw);
  if (chat && !chat.disabled) {
    return (
      <>
        <div
          className={clsx('flex cursor-pointer flex-row items-center p-2 hover:bg-blue-300', {
            'bg-blue-500': isActive
          })}
          onClick={() => dispatch(setCurrentChatUid(chatUid))}
        >
          <Avatar chatUid={chatUid}></Avatar>
          <div className="flex flex-grow flex-col p-2">
            <div className={clsx('flex flex-row justify-between')}>
              <div className={clsx({ 'blur-sm': nsfw })}>{chat.name}</div>
              <div> {chat.unreadCount} </div>
            </div>
          </div>
        </div>
      </>
    );
  } else {
    return <></>;
  }
};

const Head = () => {
  const chatAll = useAppSelector(Selectors.selectSummaryChat);
  const dispatch = useAppDispatch();

  return (
    <>
      <div
        className="flex flex-row items-center bg-gray-800 p-2 shadow"
        onClick={() => dispatch(setCurrentChatUid(chatAll.uid))}
      >
        <Avatar chatUid={chatAll.uid}></Avatar>
        <div className="flex flex-grow flex-col p-2 ">
          <div className="flex flex-row justify-between">
            <div>{chatAll.name}</div>
            <div>{chatAll.unreadCount}</div>
          </div>
        </div>
      </div>
    </>
  );
};
