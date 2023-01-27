package com.kent.tg.daemon;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.kent.tg.service.impl.DownloadServiceImpl;
import it.tdlight.jni.TdApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class UpdateFileListener {

    private final Cache<String, TdApi.File> downloadingFiles = CacheBuilder.newBuilder().expireAfterWrite(60, TimeUnit.SECONDS).build();

    @Autowired
    private DownloadServiceImpl downloadService;
    @Autowired
    private VideoDownloadTask downloadTask;


    @EventListener
    public void onUpdateFile(TdApi.UpdateFile event) {
        /**
         *
         * NOTICE:
         * 1. TdApi.UpdateFile 在下載大檔時，會被高頻觸發 2 次以上， 第一次 download size 0， local path 為空
         * 2. 小檔可能只觸發一次
         * 3. 如果檔案已經下載到本地， 就不會被觸發
         */
        TdApi.UpdateFile updateFile = event;
        TdApi.File file = updateFile.file;
        String uniqueId = file.remote.uniqueId;
        if (file.local.isDownloadingCompleted) {
            downloadService.updateDownloadStatus(file);
            downloadingFiles.invalidate(uniqueId);
        }
        downloadingFiles.put(uniqueId, file);
        downloadTask.removeFromQueueByFileUniqueId(uniqueId);
    }

    public Map<String, TdApi.File> getDownloadingFiles() {
        return downloadingFiles.asMap();
    }
}
