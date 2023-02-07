import { useAppDispatch, useAppSelector } from '@/app/hooks';
import { Popover } from '@headlessui/react';
import { ArrowSmallDownIcon } from '@heroicons/react/24/solid';
import clsx from 'clsx';
import React, { useState } from 'react';
import { interval, tap } from 'rxjs';
import { DownloadSlice } from '../stores';
import { DownloadItem } from '../types';

const useDownloadInfo = (items: DownloadItem[]) => {
  const byteToMega = (bytes: number) => {
    const megabytes = bytes / Math.pow(1024, 2);
    return megabytes.toFixed(1);
  };

  const fileSize = items.length == 0 ? 0 : items.map((val) => val.size).reduce((a, b) => a + b);
  const downloadedSize = items.length == 0 ? 0 : items.map((val) => val.downloadedSize ?? 0).reduce((a, b) => a + b);

  const total = byteToMega(fileSize);
  const downloaded = byteToMega(downloadedSize);

  return { total, downloaded };
};

export const DownloadStatus = () => {
  const dispatch = useAppDispatch();
  const items = useAppSelector(DownloadSlice.selectAllDownloadItems);
  const [showPopover, setShowPopover] = useState(false);
  const { total, downloaded } = useDownloadInfo(items);

  React.useEffect(() => {
    const sub = interval(3000)
      .pipe(
        tap(() => {
          dispatch(DownloadSlice.fetchDownloadItems());
        })
      )
      .subscribe();
    return () => sub.unsubscribe();
  }, []);

  return (
    <>
      <Popover className="relative">
        <Popover.Button onMouseEnter={() => setShowPopover(true)} onMouseLeave={() => setShowPopover(false)}>
          <ArrowSmallDownIcon
            className={clsx(
              'h-4 w-4 animate-pulse ',
              { 'animate-pulse': items.length > 0 },
              { 'text-gray-700': items.length === 0 }
            )}
          />
        </Popover.Button>
        {showPopover && (
          <Popover.Panel static className="absolute -left-40 z-10 rounded-lg bg-white p-4 shadow-md">
            <div className="text-sm font-medium text-gray-700">
              <dl>
                <dt>{items.length} items</dt>
                <dt>
                  {downloaded}/{total} MB
                </dt>
              </dl>
            </div>
          </Popover.Panel>
        )}
      </Popover>
    </>
  );
};
