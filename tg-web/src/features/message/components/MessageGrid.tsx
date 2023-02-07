import { Card } from '@/app/components';
import { useAppDispatch, useAppSelector } from '@/app/hooks';
import { selectTopMessages } from '@/app/stores/selectors';
import { ConfigSlice } from '@/features/misc';
import clsx from 'clsx';
import React, { ForwardedRef, forwardRef, useImperativeHandle, useRef } from 'react';
import { MessageInfo, MessagePhoto, MessageThumbnail } from '.';
import { useMessage } from '../hooks/useDumpMessage';
import { selectMessageByUid } from '../stores/message.slice';
import { setCurrentMessageUid } from '../stores/messageUI.slice';
import { MessageType } from '../types';

export type ScrollToHandle = {
  toTop: () => void;
  toBottom: () => void;
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

type MessageGridProps = {
  columnCount: number;
};

export const MessageGrid = forwardRef<ScrollToHandle, MessageGridProps>(function MessageGrid2(
  { columnCount = 6 },
  ref
) {
  const messages = useAppSelector(selectTopMessages);

  // use tailwind safe_list
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

  const col = toGridColumn(columnCount);
  const { topRef, bottomRef } = useScrollTo(ref);

  return (
    <>
      <div ref={topRef}></div>
      <div className={clsx(`grid gap-4 overflow-y-auto px-6 py-10`, col)}>
        {messages &&
          messages.map((message) => {
            return (
              <React.Fragment key={message.uid}>
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
  const debug = useAppSelector(ConfigSlice.selectDebug);
  const dump = useMessage(messageUid);
  return (
    <>
      <Card>
        <Card.Content
          onClick={(e) => {
            if (e.ctrlKey) {
              if (debug) {
                console.log(JSON.stringify(dump, undefined, 2));
              }
            } else {
              dispatch(setCurrentMessageUid(messageUid));
            }
          }}
        >
          <div className="relative">
            {message?.type === MessageType.Photo && <MessagePhoto messageUid={messageUid} />}
            {message?.type === MessageType.Video && <MessageThumbnail messageUid={messageUid} />}
          </div>
        </Card.Content>
        <Card.Footer>{message && <MessageInfo messageUid={message.uid} />}</Card.Footer>
      </Card>
    </>
  );
};
