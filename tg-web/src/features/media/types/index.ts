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

export interface TdFile {
  id: number;
  size: number;
  expectedSize: number;
  local: Local;
  remote: Remote;
  constructor: number;
}

export interface Local {
  path: string;
  canBeDownloaded: boolean;
  canBeDeleted: boolean;
  isDownloadingActive: boolean;
  isDownloadingCompleted: boolean;
  downloadOffset: number;
  downloadedPrefixSize: number;
  downloadedSize: number;
  constructor: number;
}

export interface Remote {
  id: string;
  uniqueId: string;
  isUploadingActive: boolean;
  isUploadingCompleted: boolean;
  uploadedSize: number;
  constructor: number;
}

export interface DownloadItem {
  messageUid: number;
  fileId: number;
  size: number;
  downloadedSize: number;
}
