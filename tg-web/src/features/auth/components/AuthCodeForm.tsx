import { Spinner } from '@/app/components';
import { useAppDispatch, useAppSelector } from '@/app/hooks';
import clsx from 'clsx';
import React from 'react';
import { updateAuthCode } from '../stores/auth.slice';
import { ICON_BUTTON_STYLE, INPUT_STYLE } from './style';

export const AuthCodeForm = ({ phoneNum }: { phoneNum: string }) => {
  const [authCode, setAuthCode] = React.useState<string>('');
  const status = useAppSelector((state) => state.auth.authCodeStatus);
  const dispatch = useAppDispatch();

  const handleChange = (event: React.FormEvent<HTMLInputElement>) => {
    const value = event.currentTarget.value;
    setAuthCode(value);
  };

  const handleSendCode = (event: any) => {
    dispatch(updateAuthCode(authCode));
  };
  return (
    <>
      <form action="" className="mt-6 mb-0 w-72 space-y-4">
        <h1 className="text-lg font-medium text-gray-100">+{phoneNum}</h1>
        <p className="text-sm text-sky-200">
          A code was sent via Telegram to you other device, if you have any connected
        </p>
        <div>
          <div className="relative mt-1 flex w-full flex-row gap-2">
            <input
              type="text"
              className={clsx('w-full', INPUT_STYLE)}
              placeholder="Code"
              name="code"
              value={authCode}
              onChange={handleChange}
            />
          </div>
        </div>

        {/* <div className="text-sky-500">Send code by SMS</div> */}
        {status === 'failed' ? <div className="text-red-600">Invalid Auth Code</div> : undefined}
        <button
          type="button"
          className={clsx('w-full ', ICON_BUTTON_STYLE)}
          disabled={status === 'loading'}
          onClick={handleSendCode}
        >
          <div className="flex-shrink-0 flex-grow-0">
            {status === 'loading' ? <Spinner size="sm" className="ml-2"></Spinner> : undefined}
          </div>
          <div className="flex-grow">Send</div>
        </button>
      </form>
    </>
  );
};
