const storagePrefix = 'tg_';

const storage = {
  getNsfw: (): boolean => {
    const nsfw = window.localStorage.getItem(`${storagePrefix}nsfw`);
    if (nsfw == undefined) {
      window.localStorage.setItem(`${storagePrefix}nsfw`, 'true');
    }
    return nsfw ? nsfw === 'true' : false;
  },
  setNsfw: (nsfw: boolean) => {
    window.localStorage.setItem(`${storagePrefix}nsfw`, JSON.stringify(nsfw));
  },
  getDebug: (): boolean => {
    const debug = window.localStorage.getItem(`${storagePrefix}debug`);
    if (debug == undefined) {
      window.localStorage.setItem(`${storagePrefix}debug`, 'true');
    }
    return debug ? debug === 'true' : false;
  },
  setDebug: (debug: boolean) => {
    window.localStorage.setItem(`${storagePrefix}debug`, JSON.stringify(debug));
  },
  getPageSize: (): number => {
    let pageSize = window.localStorage.getItem(`${storagePrefix}pageSize`);
    if (pageSize == undefined) {
      pageSize = '100';
      window.localStorage.setItem(`${storagePrefix}pageSize`, pageSize);
    }
    return parseInt(pageSize);
  },
  setPageSize: (pageSize: number) => {
    window.localStorage.setItem(`${storagePrefix}pageSize`, pageSize.toString());
  }
};

export default storage;
