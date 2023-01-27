package com.kent.tg.service.impl;

import com.kent.base.domain.DomainUtil;
import com.kent.tg.client.Telegram;
import com.kent.tg.client.TgFileUtils;
import com.kent.tg.service.FileService;
import com.kent.tg.service.MessagePhotoService;
import it.tdlight.client.Result;
import it.tdlight.jni.TdApi;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;


@Service
public class DownloadServiceImpl {

    private final Logger logger = LoggerFactory.getLogger(DownloadServiceImpl.class);
    @Autowired
    private Telegram telegram;
    @Autowired
    private FileService fileService;
    @Autowired
    private MessagePhotoService messagePhotoService;

    public void asyncDownload(TdApi.File file) {
        int priority = 1;
        int offset = 0;
        int limit = 0;
        boolean synchronous = false;
        TdApi.DownloadFile request = new TdApi.DownloadFile(file.id, priority, offset, limit, synchronous);
        logger.info("Send download file request: {} , file: {}", DomainUtil.convertPojoToMap(request), DomainUtil.convertPojoToMap(file));
        telegram.sendAsynchronously(request);
    }


    public void updateDownloadStatus(TdApi.File file) {
        com.kent.tg.domain.File f = fileService.findOneByFileUniqueId(file.remote.uniqueId).orElse(new com.kent.tg.domain.File());
        f.setFileSize(file.size);
        f.setFileUniqueId(file.remote.uniqueId);
        f.setLocalFilePath(getRelativePath(file));
        f.setLastModified(new Date());
        f.setExist(true);

        if (f.getUid() != null) {
            fileService.updateById(f);
        } else {
            fileService.save(f);
        }

        logger.info("Download file - uniqueId: {}, path: {}, size: {}/{}", file.remote.uniqueId, file.local.path, file.local.downloadedSize, file.remote.uploadedSize);
    }

    private String getRelativePath(TdApi.File file) {
        Path localPath = Paths.get(file.local.path);
        String path = TgFileUtils.getInstance().getPathByName(TgFileUtils.SESSION_HOME).relativize(localPath).toString().replaceAll("\\\\", "/");
        return path;
    }

    public void downloadPhotoFromMessage(TdApi.Message msg) {
        TdApi.MessageContent content = msg.content;
        if (content instanceof TdApi.MessagePhoto mp) {
            TdApi.PhotoSize photoSize = messagePhotoService.getLastPhotoSize(mp);
//            sendDownloadFileRequestById(photoSize.photo);
            asyncDownload(photoSize.photo);
        } else if (content instanceof TdApi.MessageVideo mv) {
//            sendDownloadFileRequestById(mv.video.thumbnail.file);
            asyncDownload(mv.video.thumbnail.file);
        } else {
            logger.warn("No Photo to Download for Message: {} - {} : {} ", content.getConstructor(), content.getClass(), StringUtils.abbreviate(content.toString().replaceAll("\n", " "), 1000));
        }
    }

    public void syncDownload(TdApi.File file) {
        logger.info("Download file id: {} - {}", file.id, file.remote.uniqueId);
        int priority = 1;
        int offset = 0;
        int limit = 0;
        boolean synchronous = true;
        TdApi.DownloadFile request2 = new TdApi.DownloadFile(file.id, priority, offset, limit, synchronous);
        Result<TdApi.File> objectResult = telegram.sendSynchronously(request2, TdApi.File.class, 30);
        if (objectResult.isError()) {
            logger.error("Download file fail: {}", file.id);
        } else {
            TdApi.File file2 = objectResult.get();
                    logger.info("Download file success: {} - {}", file2.remote.uniqueId, file2.local.path);
            updateDownloadStatus(file2);
        }
    }

    public void downloadVideoFromMessage(TdApi.Message msg) {
        TdApi.MessageContent content = msg.content;
        if (content instanceof TdApi.MessageVideo mv) {
            logger.info("Send download video file request:  file name: {} - {}", mv.video.fileName, DomainUtil.convertPojoToMap(mv.video.video));
//            sendDownloadFileRequestById(mv.video.video);
            asyncDownload(mv.video.video);
        } else {
            logger.warn("No Video to Download for Message: {} - {} : {}", content.getConstructor(), content.getClass(), StringUtils.abbreviate(content.toString().replaceAll("\n", " "), 1000));
            new TdApi.Error(404, "File Not Found");
        }
    }


    public void downloadPhotoFromChat(TdApi.Chat chat) {
        if (chat.photo == null) {
            logger.warn("Chat has no profile photo: {}", chat.title);
            return;
        }
        TdApi.File file = chat.photo.small;
        asyncDownload(file);
    }

    /**
     * TdApi.File  無法直接下載， 需要透過 file id, id 在不同的 session 下會不同， 需要透過 message 間接取得
     *
     * @param messageId
     * @param chatId
     */
    public void syncDownloadPhoneFromMessage(long messageId, long chatId) {
        TdApi.Message message = getMessage(messageId, chatId);
        if (message.content instanceof TdApi.MessageVideo mv) {
            if (mv.video.thumbnail != null) {
                syncDownload(mv.video.thumbnail.file);
            }
        } else if (message.content instanceof TdApi.MessagePhoto mp) {
            TdApi.PhotoSize photoSize = messagePhotoService.getLastPhotoSize(mp);
            if (photoSize != null) {
                syncDownload(photoSize.photo);
            }
        } else {
            logger.error("Unexpected Message type for download file", message);
        }
    }

    @NotNull
    private TdApi.Message getMessage(long messageId, long chatId) {
        var request = new TdApi.GetMessage(chatId, messageId);
        TdApi.Message message = (TdApi.Message) telegram.sendSynchronously(request, TdApi.Object.class, 30).get();
        logger.debug("Message which have missing file: {}", message);
        return message;
    }
}
