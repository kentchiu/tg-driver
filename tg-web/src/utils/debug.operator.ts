import { tap } from 'rxjs/operators';

export function debug<T>(tag: string) {
  return tap({
    next(value: T) {
      console.log(`%c[${tag}: Next]`, 'background: #009688; color: #fff; padding: 3px; font-size: 9px;', value);
    },
    error(error) {
      console.log(`%[${tag}: Error]`, 'background: #E91E63; color: #fff; padding: 3px; font-size: 9px;', error);
    },
    complete() {
      console.log(`%c[${tag}]: Complete`, 'background: #00BCD4; color: #fff; padding: 3px; font-size: 9px;');
    }
  });
}
