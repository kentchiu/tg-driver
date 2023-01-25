import { Card } from '@/app/components';
import { useAppDispatch, useAppSelector } from '@/app/hooks';

import { BanRuleSlice, MessageUiSlice } from '@/app/features/message/stores';
import { MessageType } from '@/app/features/message/types';
import { selectTopMessages } from '@/app/stores/selectors';
import clsx from 'clsx';
import React, { ForwardedRef, forwardRef, useImperativeHandle, useRef } from 'react';
import toast from 'react-hot-toast';
import { ContextMenu, MessageInfo, MessagePhoto, MessageThumbnail } from '.';
import { selectMessageByUid } from '../stores/message.slice';

export type ScrollToHandle = {
  toTop: () => void;
  toBottom: () => void;
};

type MessageGridProps = {
  //
};

const useScrollTo = (ref: ForwardedRef<ScrollToHandle>) => {
  const topRef = useRef<HTMLDivElement>(null);
  const bottomRef = useRef<HTMLDivElement>(null);

  useImperativeHandle(ref, () => ({
    toTop() {
      if (topRef.current) {
        topRef.current.scrollIntoView({ behavior: 'auto', block: 'nearest' });
      }
    },
    toBottom() {
      if (bottomRef.current) {
        bottomRef.current.scrollIntoView({ behavior: 'auto', block: 'end' });
      }
    }
  }));
  return { topRef, bottomRef };
};

export const MessageGrid = forwardRef<ScrollToHandle, MessageGridProps>(function MessageGrid2(props, ref) {
  const messages = useAppSelector(selectTopMessages);
  const toGridColumn = (columnSize: number) => {
    let col = 'grid-cols-6';
    switch (columnSize) {
      case 1:
        col = 'grid-cols-1';
        break;
      case 2:
        col = 'grid-cols-2';
        break;
      case 3:
        col = 'grid-cols-3';
        break;
      case 4:
        col = 'grid-cols-4';
        break;
      case 5:
        col = 'grid-cols-5';
        break;
      case 6:
        col = 'grid-cols-6';
        break;
      case 7:
        col = 'grid-cols-7';
        break;
      case 8:
        col = 'grid-cols-8';
        break;
      case 9:
        col = 'grid-cols-9';
        break;
      case 10:
        col = 'grid-cols-10';
        break;
      case 11:
        col = 'grid-cols-11';
        break;
      case 12:
        col = 'grid-cols-12';
        break;
      default:
        col = 'grid-cols-6';
    }
    return col;
  };

  const columnSize = useAppSelector((state) => state.messageUi.columnSize);
  const col = toGridColumn(columnSize);
  const { topRef, bottomRef } = useScrollTo(ref);

  return (
    <>
      <div ref={topRef}></div>
      <div className={clsx(`grid gap-4 overflow-y-auto px-6 py-10`, col)}>
        {messages &&
          messages.map((message) => {
            return (
              <React.Fragment key={message.uid}>
                {/* <ContextMenu
                  targetId={`message-${message.uid}`}
                  message={message}
                  onBanImage={(fileUniqueId) => dispatch(BanRuleSlice.banPhoto({ fileUniqueId }))}
                  onBanKeyword={(keyword) => {
                    dispatch(BanRuleSlice.banMessage({ keyword }));
                    toast.success(`Ban keyword: [${keyword}]`);
                  }}
                /> */}
                <div id={`message-${message.uid}`}>
                  <GridItem key={message.uid} messageUid={message.uid}></GridItem>
                </div>
              </React.Fragment>
            );
          })}
      </div>

      <div ref={bottomRef}> </div>
    </>
  );
});

const GridItem = ({ messageUid }: { messageUid: number; banIds?: number[] }) => {
  const dispatch = useAppDispatch();
  const message = useAppSelector((state) => selectMessageByUid(state, messageUid));
  return (
    <>
      <Card>
        <Card.Content
          onClick={() => {
            dispatch(MessageUiSlice.setCurrentMessageUid(messageUid));
          }}
        >
          <div className="relative">
            {/* {message!.banByRules!.length > 0 ? (
              <NoSymbolIcon className="absolute z-10 h-24 w-24  text-red-900" />
            ) : undefined} */}
            {message?.type === MessageType.Photo && <MessagePhoto messageUid={messageUid} />}
            {message?.type === MessageType.Video && <MessageThumbnail messageUid={messageUid} />}
          </div>
        </Card.Content>
        <Card.Footer>{message && <MessageInfo messageUid={message.uid} />}</Card.Footer>
      </Card>
    </>
  );
};
