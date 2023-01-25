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
  lastModified: number;
  localFilePath: string;
}

export interface DownloadItem {
  path: string;
  expectedSize: number;
  downloadedSize: number;
}
