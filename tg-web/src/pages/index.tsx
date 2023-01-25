import { AuthCodeForm, AuthSlice, PhoneForm } from '@/features/auth';
import { ChatSlice } from '@/features/chat';
import { ConfigSlice } from '@/features/misc';
import Link from 'next/link';
import { useRouter } from 'next/router';
import React from 'react';
import { Spinner } from '../components';
import { useAppDispatch, useAppSelector } from '../hooks';

export const Home = () => {
  const dispatch = useAppDispatch();
  const stage = useAppSelector((state) => state.auth.stage);
  const phoneNumber = useAppSelector((state) => state.auth.phoneNumber);

  const router = useRouter();
  React.useEffect(() => {
    dispatch(AuthSlice.fetchAuthStage());

    if (stage === 'AUTHENTICATED') {
      dispatch(ChatSlice.refreshChats()).then((val) => {
        console.log('val', val);
        router.push('/main');
      });
      dispatch(ConfigSlice.isNsfw());
    }
  }, [stage, dispatch]);

  let content;
  if (stage === undefined) {
    content = <Spinner></Spinner>;
  } else if (stage === 'PHONE_NUMBER' || stage === undefined) {
    content = <PhoneForm></PhoneForm>;
  } else if (stage === 'AUTH_CODE') {
    content = <AuthCodeForm phoneNum={phoneNumber ?? ''}></AuthCodeForm>;
  } else if (stage === 'AUTHENTICATED') {
    content = <Link href="/main">Starting...</Link>;
  }
  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-gray-800 py-2 text-gray-600 ">
      <div className="mx-auto max-w-screen-xl  px-4 py-16 sm:px-6 lg:px-8">
        <div className="mx-auto max-w-lg">{content}</div>
      </div>
    </div>
  );
};

export default Home;
