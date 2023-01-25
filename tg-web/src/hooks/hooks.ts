import { AppDispatch, AppState } from '@/app/stores/index';
import { TypedUseSelectorHook, useDispatch, useSelector } from 'react-redux';

// // https://overreacted.io/making-setinterval-declarative-with-react-hooks/

// // https://overreacted.io/making-setinterval-declarative-with-react-hooks/
// export const useInterval = (callback: Function, delay: number) => {
//   const savedCallback = useRef<Function>()
//   useEffect(() => {
//     savedCallback.current = callback
//   }, [callback])
//   useEffect(() => {
//     const handler = (...args: any) => savedCallback.current?.(...args)

//     if (delay !== null) {
//       const id = setInterval(handler, delay)
//       return () => clearInterval(id)
//     }
//   }, [delay])
// }

// Use throughout your app instead of plain `useDispatch` and `useSelector`
const useAppDispatch = () => useDispatch<AppDispatch>();

const useAppSelector: TypedUseSelectorHook<AppState> = useSelector;

export { useAppDispatch, useAppSelector };
