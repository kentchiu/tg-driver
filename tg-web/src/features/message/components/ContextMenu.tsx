import { useAppSelector } from '@/app/hooks';
import { selectPhotoFileByMessageUid } from '@/app/stores/selectors';
import { useEffect, useLayoutEffect, useRef, useState } from 'react';
import { Message } from '../types';

type ContextMenuProps = {
  targetId: string;
  message: Message;
  onBanImage?: (fileUniqueId: string) => void;
  onBanKeyword?: (keyword: string) => void;
};

export const ContextMenu = ({ targetId, message, onBanImage, onBanKeyword }: ContextMenuProps) => {
  const [contextData, setContextData] = useState({ visible: false, posX: 0, posY: 0 });
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const contextRef = useRef<any>(null);
  const [selectedText, setSelectedText] = useState<string | undefined>(undefined);
  const file = useAppSelector((state) => selectPhotoFileByMessageUid(state, message.uid));

  const handleBanImage = () => {
    file && onBanImage && onBanImage(file?.fileUniqueId);
    setContextData({ ...contextData, visible: false });
  };
  const handleBanText = () => {
    selectedText && onBanKeyword && onBanKeyword(selectedText);
    setContextData({ ...contextData, visible: false });
  };

  useEffect(() => {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const contextMenuEventHandler = (event: any) => {
      if (document.getSelection()) {
        setSelectedText(document.getSelection()?.toString());
      }
      const targetElement = document.getElementById(targetId);
      if (targetElement && targetElement.contains(event.target)) {
        event.preventDefault();
        setContextData({ visible: true, posX: event.clientX, posY: event.clientY });
      } else if (contextRef.current && !contextRef.current.contains(event.target)) {
        setContextData({ ...contextData, visible: false });
      }
    };

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const offClickHandler = (event: any) => {
      if (contextRef.current && !contextRef.current.contains(event.target)) {
        setContextData({ ...contextData, visible: false });
      }
    };

    document.addEventListener('contextmenu', contextMenuEventHandler);
    document.addEventListener('click', offClickHandler);
    return () => {
      document.removeEventListener('contextmenu', contextMenuEventHandler);
      document.removeEventListener('click', offClickHandler);
    };
  }, [contextData, targetId]);

  useLayoutEffect(() => {
    if (contextData.posX + contextRef.current?.offsetWidth > window.innerWidth) {
      setContextData({ ...contextData, posX: contextData.posX - contextRef.current?.offsetWidth });
    }
    if (contextData.posY + contextRef.current?.offsetHeight > window.innerHeight) {
      setContextData({ ...contextData, posY: contextData.posY - contextRef.current?.offsetHeight });
    }
  }, [contextData]);

  return (
    <div
      ref={contextRef}
      className="absolute z-30 mt-2 w-72 rounded-md bg-slate-600"
      style={{ display: `${contextData.visible ? 'block' : 'none'}`, left: contextData.posX, top: contextData.posY }}
    >
      <ul className={`optionsList py-1`}>
        <li
          className={`flex cursor-pointer  whitespace-nowrap p-3  shadow-sm hover:bg-slate-500`}
          onClick={handleBanImage}
        >
          Ban Image
        </li>

        {selectedText ? (
          <li
            className={`flex cursor-pointer  whitespace-nowrap p-3  shadow-sm hover:bg-slate-500`}
            onClick={handleBanText}
          >
            Ban Text:
            <div className="overflow-hidden text-ellipsis whitespace-nowrap">{selectedText}</div>
          </li>
        ) : undefined}
      </ul>
    </div>
  );
};
