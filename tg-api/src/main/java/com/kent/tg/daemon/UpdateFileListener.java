package com.kent.tg.daemon;

import com.kent.tg.client.LoggerUtils;
import com.kent.tg.domain.File;
import com.kent.tg.facade.DownloadFacade;
import com.kent.tg.service.FileService;
import it.tdlight.jni.TdApi;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component
public class UpdateFileListener {

    private final Logger logger = LoggerFactory.getLogger(UpdateFileListener.class);
    @Autowired
    private DownloadFacade downloadFacade;
    @Autowired
    private FileService fileService;

    @EventListener
    public void onUpdateFile(TdApi.UpdateFile event) {
        /*
         * NOTICE:
         * 1. TdApi.UpdateFile is triggered more than 2 times during file download, the first time with a download size
         *    of 0 and an empty local path.
         * 2. If the file has already been downloaded to the local machine, it will not be triggered.
         * 3. After the file has been downloaded to the local machine, if this event is received,
         *    it usually means the file has been removed and can be determined by the
         *    local path, isDownloadCompleted = false, and downloadSize = 0.
         */
        TdApi.UpdateFile updateFile = event;
        TdApi.File file = updateFile.file;
        downloadFacade.updateDownloadStatus(file);
        markFileAsDeletedIfNecessary(file);
    }

    private void markFileAsDeletedIfNecessary(TdApi.File file) {
        boolean isNewOrDelete = StringUtils.isBlank(file.local.path) && file.local.downloadedSize == 0 && !file.local.isDownloadingCompleted;
        if (isNewOrDelete) {
            Optional<File> fileOptional = fileService.findOneByFileUniqueId(file.remote.uniqueId);
            if (fileOptional.isEmpty()) {
                return;
            }
            if (StringUtils.startsWith(fileOptional.get().getLocalFilePath(), "data/profile_photos/")) {
                return;
            }
            File f = fileOptional.get();
            f.setExist(false);
            f.setLastModified(new Date());
            fileService.updateById(f);
            logger.info("File has been deleted: {}", LoggerUtils.shrinkText(f.toString(), 1000));
        }
    }

}
