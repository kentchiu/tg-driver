export interface Message {
  uid: number;
  date: number;
  messageId: number;
  readAt?: string | undefined;
  type: MessageType;
  // normalize field
  text?: number;
  chat: number;
  photo?: number;
  video?: number;
}

export interface Message {
  banByRules?: BanRule[];
}

export enum MessageType {
  Photo = 'MessagePhoto',
  Video = 'MessageVideo'
}

export interface MessageText {
  id: number;
  text: string;
}

export interface MessageVideo {
  uid: number;
  duration: number;
  caption: string;
  fileName: string;
  fileSize: number;
  // normalize files
  chat: number;
  message: number;
  photo: number;
  video: number;
}

// export interface MessageVideo {
//   /**
//    * 重複的信息數量
//    */
//   count?: number;
//   /**
//    * 重複的 MessageVideo Uid
//    * 重複的原因是同一個影片 PO 在多個 channel
//    */
//   uids?: string;
// }

export interface BanRule {
  uid: number;
  rule: string;
  property: 'caption' | 'fileUniqueId';
}
