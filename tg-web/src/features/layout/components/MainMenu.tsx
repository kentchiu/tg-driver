import Link from 'next/link';

export const MainMenu = () => {
  return (
    <>
      <div className="flex ">
        <Link className="pl-5" href="/main">
          Home
        </Link>
        <Link className="pl-5" href="/video">
          Video
        </Link>
        <Link className="pl-5" href="/player">
          Player
        </Link>
      </div>
    </>
  );
};
