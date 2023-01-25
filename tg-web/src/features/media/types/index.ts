export interface Photo {
  height: number;
  uid: number;
  width: number;
  caption: string;
  // normalize fields
  file: number;
}

export interface Video {
  duration: number;
  uid: number;
  fileName: string;
  fileSize: number;
  width: number;
  caption: string;
  mimeType: string;
  height: number;
  // normalize fields
  file: number;
}

export interface File {
  uid: number;
  exist: boolean;
  fileSize: number;
  fileUniqueId: string;
  lastModified: Date;
  localFilePath: string;
}

/**
 * 實體檔案屬性
 */
// export interface File {
//   existed: boolean;
//   createdAt?: number;
//   modifiedAt?: number;
// }

export interface DownloadItem {
  path: string;
  expectedSize: number;
  downloadedSize: number;
}
