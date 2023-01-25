import dayjs from 'dayjs';
import duration from 'dayjs/plugin/duration';

export const formatDate = (date: Date) => dayjs(date).format('M/DD HH:mm');

dayjs.extend(duration);

export const toDuration = (milliseconds: number) => dayjs.duration(milliseconds);
