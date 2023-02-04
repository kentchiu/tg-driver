import { useAppDispatch, useAppSelector } from '@/app/hooks';
import { Selectors } from '@/app/stores';
import { ChatList, ChatSlice, ChatUiSlice, CHAT_ID_ALL } from '@/features/chat';
import { MainMenu } from '@/features/layout';
import { MessageGrid, MessageSlice, MessageUiSlice, Popover, ScrollToHandle } from '@/features/message';
import { ConfigSlice } from '@/features/misc';
import { CheckIcon, ChevronDoubleDownIcon } from '@heroicons/react/24/solid';
import React, { useRef } from 'react';
import { createSelector } from 'reselect';

const useFetchingEffect = () => {
  const dispatch = useAppDispatch();
  const currentChat = useAppSelector(Selectors.selectCurrentChat);

  React.useEffect(() => {
    dispatch(ChatSlice.fetchChats());
    dispatch(ConfigSlice.isNsfw());
  }, [dispatch]);

  // fetch message
  React.useEffect(() => {
    if (currentChat && currentChat.uid) {
      dispatch(MessageSlice.fetchUnreadMessageOfChat(currentChat.uid));
    }
  }, [currentChat]);
};

const MainLayoutPage = () => {
  useFetchingEffect();
  const dispatch = useAppDispatch();
  const currentMessage = useAppSelector(Selectors.selectCurrentMessage);
  const messageGridRef = useRef<ScrollToHandle>(null);
  const messageUids = useAppSelector(selectMessageUidsOfCurrentChat);
  const columnCount = useAppSelector((state) => state.messageUi.columnCount);

  const handleMarkAllAsRead = () => {
    if (messageGridRef.current) {
      messageGridRef.current.toTop();
    }
  };

  const handleScrollToEnd = () => {
    if (messageGridRef.current) {
      messageGridRef.current.toBottom();
    }
  };

  return (
    <>
      <div className="flex h-screen max-h-screen flex-row bg-gray-900 text-gray-200">
        <div className="flex flex-1 flex-col overflow-hidden">
          <header className="flex items-center justify-between border-b border-gray-500 bg-gray-900 p-4">
            <div className="flex">
              <MainMenu></MainMenu>
            </div>
            <div className="flex">
              <input
                type="range"
                min="1"
                max="12"
                onChange={(event) => {
                  const value = event.target.valueAsNumber;
                  dispatch(MessageUiSlice.setColumnSize(value));
                }}
                value={columnCount}
              ></input>
            </div>
          </header>
          <div className="flex h-full">
            <nav className="flex h-full w-96">
              <div className="scrollbar w-full flex-col overflow-y-scroll">
                <ChatList></ChatList>
              </div>
            </nav>
            <main className="scrollbar mb-14 flex w-full flex-col overflow-scroll  overflow-x-hidden bg-gray-900">
              <div className="mx-auto flex w-full px-6 py-8">
                <div className="flex h-full w-full flex-col">
                  <div className="top-30 absolute z-30 m-3 flex justify-start">
                    <Buttons markAllAsRead={handleMarkAllAsRead} scrollToBottom={handleScrollToEnd}></Buttons>
                  </div>
                  <MessageGrid columnCount={columnCount} ref={messageGridRef}></MessageGrid>
                </div>
              </div>
            </main>
          </div>
          {currentMessage && <Popover messageUid={currentMessage?.uid} messageUids={messageUids}></Popover>}
        </div>
      </div>
    </>
  );
};

const selectMessageUidsOfCurrentChat = createSelector(
  [MessageSlice.selectAllMessages, ChatUiSlice.selectCurrentChatUid],
  (messages, chatUid) => {
    return messages.filter((msg) => msg.chat === chatUid).map((msg) => msg.uid);
  }
);

type ButtonsProps = {
  markAllAsRead?: () => void;
  scrollToBottom?: () => void;
};

const Buttons = ({ markAllAsRead, scrollToBottom }: ButtonsProps) => {
  const dispatch = useAppDispatch();
  const currentChat = useAppSelector(Selectors.selectCurrentChat);
  const chats = useAppSelector(ChatSlice.selectAllChats);
  const messages = useAppSelector(Selectors.selectTopMessages);
  const messageIds = messages.map((val) => val.uid);

  const handleMarkAllAsRead = () => {
    if (currentChat) {
      const index = chats.findIndex((c) => c.uid === currentChat.uid);
      let nextChatId = undefined;
      if (messageIds.length >= currentChat.unreadCount) {
        if (index < chats.length) {
          nextChatId = chats[index + 1]?.uid;
        } else {
          nextChatId = CHAT_ID_ALL;
        }
      }
      dispatch(ChatSlice.markChatAsRead({ chatUid: currentChat.uid, nextChatId: nextChatId, messageUids: messageIds }));
      markAllAsRead && markAllAsRead();
    }
  };

  const handleScrollToBottom = () => {
    scrollToBottom && scrollToBottom();
  };

  return (
    <>
      <ul className="grid grid-flow-row grid-cols-1 grid-rows-1 ">
        <li className="flex items-center" onClick={() => handleScrollToBottom()}>
          <div className="flex h-12 w-12 content-center items-center justify-center rounded-full bg-gradient-to-br from-gray-300 to-gray-900 fill-current text-gray-50 opacity-50 hover:opacity-100 ">
            <ChevronDoubleDownIcon className="h-8 w-8 text-gray-50" />
          </div>
        </li>
        <li className="flex items-center" onClick={() => handleMarkAllAsRead()}>
          <div className="flex h-12 w-12 content-center items-center justify-center rounded-full bg-gradient-to-br from-gray-300 to-gray-900 fill-current text-gray-50  opacity-70 hover:opacity-100 ">
            <CheckIcon className="h-8 w-8 text-gray-50" />
          </div>
        </li>
      </ul>
    </>
  );
};

export default MainLayoutPage;
