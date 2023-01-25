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

export interface BanRule {
  uid: number;
  rule: string;
  property: 'caption' | 'fileUniqueId';
}
