import { Spinner } from '@/app/components';
import { useAppDispatch, useAppSelector } from '@/app/hooks';
import clsx from 'clsx';
import React, { KeyboardEvent } from 'react';
import { AuthSlice } from '../stores';
import { fetchAuthStage } from '../stores/auth.slice';
import { ICON_BUTTON_STYLE, INPUT_STYLE } from './style';

export const PhoneForm = () => {
  const [countryCode, setCountryCode] = React.useState<string>('');
  const [phoneNum, setPhoneNum] = React.useState<string>('');
  const dispatch = useAppDispatch();
  const status = useAppSelector((state) => state.auth.phoneNumberStatus);
  const error = useAppSelector((state) => state.auth.phoneNumberError);

  const handleChange = (event: React.FormEvent<HTMLInputElement>) => {
    const value = event.currentTarget.value;
    const name = event.currentTarget.name;
    if (name === 'countryCode') {
      setCountryCode(value);
    }
    if (name === 'phoneNum') {
      setPhoneNum(value);
    }
  };

  const handleNext = (event: any) => {
    dispatch(AuthSlice.updatePhoneNumber(`${countryCode}${phoneNum}`));
    // Workaround: update auth stage after updatePhoneNumber
    setTimeout(() => {
      dispatch(fetchAuthStage());
    }, 10000);
  };

  const handleKeyDown = (event: KeyboardEvent) => {
    if (event.code === 'Enter') {
      dispatch(AuthSlice.updatePhoneNumber(`${countryCode}${phoneNum}`));
      // Workaround: update auth stage after updatePhoneNumber
      setTimeout(() => {
        dispatch(fetchAuthStage());
      }, 10000);
    }
  };

  return (
    <>
      <form action="" className="flex w-72 flex-col space-y-10">
        <div>
          <h1 className="text-lg font-medium text-gray-100">Your Phone Number</h1>
          <p className="text-sm text-sky-200">Enter you phone number with country code</p>
        </div>
        <div>
          <div className="relative mt-1 flex w-full flex-row gap-2">
            <input
              type="text"
              className={clsx('w-1/4 text-xl', INPUT_STYLE)}
              placeholder="code"
              name="countryCode"
              value={countryCode}
              onChange={handleChange}
              onKeyDown={handleKeyDown}
            />
            <input
              type="tel"
              className={clsx('w-3/4 p-4 text-xl', INPUT_STYLE)}
              placeholder="--- --- ---"
              name="phoneNum"
              value={phoneNum}
              onChange={handleChange}
              onKeyDown={handleKeyDown}
            />
          </div>
        </div>
        {status === 'failed' ? <div className="text-red-600"> {error as string}</div> : undefined}
        <button
          type="button"
          className={clsx('w-full', ICON_BUTTON_STYLE)}
          disabled={status === 'loading'}
          onClick={handleNext}
        >
          <div className="flex-shrink-0 flex-grow-0">
            {status === 'loading' ? <Spinner size="sm" className="ml-2"></Spinner> : undefined}
          </div>
          <div className="flex-grow">Next</div>
        </button>
      </form>
    </>
  );
};
