import clsx from 'clsx';
import Link from 'next/link';
import { useRouter } from 'next/router';

export const MainMenu = () => {
  const router = useRouter();
  const activeMenu = router.pathname;
  console.log(activeMenu);
  return (
    <>
      <div className="flex gap-5 text-gray-500 first:pl-5 ">
        <Link
          className={clsx('hover:font-bold hover:text-gray-300', { 'text-gray-50': activeMenu === '/main' })}
          href="/main"
        >
          Home
        </Link>
        <Link
          className={clsx('hover:font-bold hover:text-gray-300', { 'text-gray-50': activeMenu === '/video' })}
          href="/video"
        >
          Video
        </Link>
        <Link
          className={clsx('hover:font-bold hover:text-gray-300', { 'text-gray-50': activeMenu === '/player' })}
          href="/player"
        >
          Player
        </Link>
      </div>
    </>
  );
};
