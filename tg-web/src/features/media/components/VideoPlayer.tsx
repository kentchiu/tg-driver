import React from 'react';
import ReactPlayer from 'react-player';
export const VideoPlayer = ({ src, poster }: { src: string; poster?: string }) => {
  const [muted, setMuted] = React.useState<boolean>(true);
  const [volume, setVolume] = React.useState<number | undefined>(undefined);
  const [playing, setPlaying] = React.useState<boolean>(false);

  const playerRef = React.useRef<ReactPlayer>(null);

  const handlePlayOrPause = React.useCallback(() => {
    setPlaying(!playerRef.current?.props.playing);
  }, [src]);

  const handleVolumeUp = React.useCallback(() => {
    const volume = playerRef.current?.props.volume ?? 0;
    let newVolume = volume + 0.1;
    if (volume > 0.9) {
      newVolume = 1;
    }
    setVolume(newVolume);
    setMuted(false);
  }, [src]);

  const handleVolumeDown = React.useCallback(() => {
    const volume = playerRef.current?.props.volume ?? 0;
    let newVolume = volume - 0.1;
    if (newVolume < 0.1) {
      newVolume = 0;
    }
    setVolume(newVolume);
  }, [src]);

  const handleForward = React.useCallback(() => {
    const player = playerRef.current;
    if (player) {
      const currentTime = player.getCurrentTime() ?? 0;
      const newTime = currentTime + 10;
      const duration = player.getDuration();

      if (newTime >= duration) {
        setPlaying(false);
      }
      player.seekTo(newTime);
    }
  }, [src]);

  const handleBack = React.useCallback(() => {
    const player = playerRef.current;
    if (player) {
      const currentTime = player.getCurrentTime() ?? 0;
      let newTime = currentTime - 10;
      if (newTime <= 0) {
        newTime = 0;
      }
      player.seekTo(newTime);
    }
  }, [src]);

  React.useEffect(() => {
    const keydownHandler = (event: KeyboardEvent) => {
      switch (event.code) {
        case 'ArrowUp':
          handleVolumeUp();
          event.preventDefault();
          break;
        case 'ArrowDown':
          handleVolumeDown();
          event.preventDefault();
          break;
        case 'ArrowLeft':
          handleBack();
          event.preventDefault();
          break;
        case 'ArrowRight':
          handleForward();
          event.preventDefault();
          break;
        case 'Space':
          handlePlayOrPause();
          event.preventDefault();
          break;
        default:
          break;
      }
    };
    document.addEventListener('keydown', keydownHandler);
    return () => document.removeEventListener('keydown', keydownHandler);
  }, [src]);

  return (
    <>
      <ReactPlayer
        ref={playerRef}
        width="100%"
        height="100%"
        url={src}
        playing={playing}
        volume={volume}
        muted={muted}
        controls={true}
        // onPlay={handlePlay}
        // onPause={handlePause}
        // onSeek={(e) => console.log('onSeek', e)}
        onError={(e) => console.log('onError', e)}
        onProgress={(p) => console.log('process', p)}
        onReady={() => console.log('onReady')}
        onStart={() => console.log('onStart')}
        // onBuffer={() => console.log('onBuffer')}
        // {...playerProps}
      ></ReactPlayer>
    </>
  );
};
