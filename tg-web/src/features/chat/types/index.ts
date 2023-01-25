/**
 *  特殊 chat id， 給虛擬的 summary chat ，使用
 */
export const CHAT_ID_ALL = 0;

export interface Chat {
  uid: number;
  /**
   * TD chat id
   */
  chatId: number;
  name: string;
  disabled: boolean;
  readCount: number;
  unreadCount: number;
  profile: number;
}
