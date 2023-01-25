const storagePrefix = 'tg_';

const storage = {
  getNsfw: (): boolean => {
    const nsfw = window.localStorage.getItem(`${storagePrefix}nsfw`);
    if (nsfw == undefined) {
      window.localStorage.setItem(`${storagePrefix}nsfw`, 'true');
    }
    return nsfw ? nsfw === 'true' : false;
  },
  setNsfw: (blur: boolean) => {
    window.localStorage.setItem(`${storagePrefix}nsfw`, JSON.stringify(blur));
  }
};

export default storage;
