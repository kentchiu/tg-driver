package com.kent.tg.facade;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.kent.base.domain.DomainUtil;
import com.kent.base.exception.DuplicateException;
import com.kent.base.exception.ResourceNotFoundException;
import com.kent.tg.client.LoggerUtils;
import com.kent.tg.client.Telegram;
import com.kent.tg.client.TgFileUtils;
import com.kent.tg.domain.File;
import com.kent.tg.domain.Message;
import com.kent.tg.domain.dto.DownloadItem;
import com.kent.tg.service.FileService;
import com.kent.tg.service.MessageService;
import it.tdlight.client.Result;
import it.tdlight.jni.TdApi;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
public class DownloadFacade {

    private final Logger logger = LoggerFactory.getLogger(DownloadFacade.class);
    private final Cache<String, DownloadItem> videoItemCache = CacheBuilder.newBuilder().expireAfterWrite(60, TimeUnit.MINUTES).build();
    @Autowired
    private Telegram telegram;
    @Autowired
    private FileService fileService;
    @Autowired
    private MessageFacade messageFacade;
    @Autowired
    private MessageService messageService;

    private synchronized File saveOrUpdate(TdApi.File file) {
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
        return f;
    }

    private String getRelativePath(TdApi.File file) {
        Path localPath = Paths.get(file.local.path);
        String path = TgFileUtils.getInstance().getPathByName(TgFileUtils.SESSION_HOME).relativize(localPath).toString().replaceAll("\\\\", "/");
        return path;
    }

    public void asyncDownloadPhotoFromMessageContent(TdApi.MessageContent messageContent) {
        TdApi.MessageContent content = messageContent;
        if (content instanceof TdApi.MessagePhoto mp) {
            TdApi.PhotoSize photoSize = messageFacade.getLastPhotoSize(mp);
            deleteFile(photoSize.photo.local.path); // NOTE: delete exist file to ensure re-download in case to works well in TdApi.UpdateFile event
            telegram.asyncDownload(photoSize.photo);
        } else if (content instanceof TdApi.MessageVideo mv) {
            if (mv.video.thumbnail != null) {
                deleteFile(mv.video.thumbnail.file.local.path); // NOTE: delete exist file to ensure re-download in case to works well in TdApi.UpdateFile event
                telegram.asyncDownload(mv.video.thumbnail.file);
            }
        }
    }

    public Result<TdApi.File> syncDownloadPhotoFromMessageContent(TdApi.MessageContent messageContent) {
        TdApi.MessageContent content = messageContent;
        if (content instanceof TdApi.MessagePhoto mp) {
            TdApi.PhotoSize photoSize = messageFacade.getLastPhotoSize(mp);
            return telegram.syncDownload(photoSize.photo);
        } else if (content instanceof TdApi.MessageVideo mv) {
            if (mv.video.thumbnail != null) {
                return telegram.syncDownload(mv.video.thumbnail.file);
            }
        }
        return Result.ofError(new ResourceNotFoundException(TdApi.MessageContent.class, "Unsupported content type:" + messageContent.getClass().getSimpleName()));
    }

    private void deleteFile(String path) {
        if (StringUtils.isNotBlank(path)) {
            // if file exist, delete it to trigger re-download event
            try {
                logger.info("Delete broken file: {}", path);
                Files.deleteIfExists(Paths.get(path));
            } catch (IOException e) {
                logger.error("Delete file fail", e);
                throw new RuntimeException(e);
            }
        }
    }

    public void asyncDownloadVideoFromMessage(TdApi.Message msg) {
        TdApi.MessageContent content = msg.content;
        if (content instanceof TdApi.MessageVideo mv) {
            logger.info("Send download video file request:  file name: {} - {}", mv.video.fileName, DomainUtil.convertPojoToMap(mv.video.video));
            telegram.asyncDownload(mv.video.video);
        } else {
            logger.warn("No Video to Download for Message: {} - {} : {}", content.getConstructor(), content.getClass(), StringUtils.abbreviate(content.toString().replaceAll("\n", " "), 1000));
            new TdApi.Error(404, "File Not Found");
        }
    }

    public void asyncDownloadPhotoFromChat(TdApi.Chat chat) {
        if (chat.photo == null) {
            logger.warn("Chat has no profile photo: {}", chat.title);
            return;
        }
        TdApi.File file = chat.photo.small;
        telegram.asyncDownload(file);
    }

    public void addDownloadItem(long messageUid) {
        Message message = messageService.getById(messageUid);
        if (message == null) {
            logger.warn("Message@{} not found", messageUid);
            throw new ResourceNotFoundException(Message.class, Long.toString(messageUid));
        } else {

            var chatId = message.getChatId();
            var messageId = message.getMessageId();

            Result<TdApi.Message> result = telegram.getMessage(messageId, chatId);

            if (result.isError()) {
                logger.error("TD Message not found for message {}", message);
                throw new ResourceNotFoundException(TdApi.Message.class, "messageId=%d, chatId=%d, messageUid=%d".formatted(messageId, chatId, messageUid));
            } else {
                TdApi.Message tdMsg = result.get();
                DownloadItem item = new DownloadItem(message, tdMsg);
                if (item.geId() != null) {
                    if (StringUtils.isNotBlank(item.getTdFile().local.path) && Files.exists(Paths.get(item.getTdFile().local.path))) {
                        logger.warn("File is download or downloaded: {}", item);
                        throw new DuplicateException("File is downloading or downloaded: " + TgFileUtils.toRelatePath(item.getTdFile().local.path));
                    } else {
                        videoItemCache.put(item.geId(), item);
                        asyncDownloadVideoFromMessage(tdMsg);
                        logger.info("Add message@{} to download queue", messageUid);
                    }
                }
            }
        }
    }

    public void downloadComplete(TdApi.File file) {
        saveOrUpdate(file);
        DownloadItem downloadItem = videoItemCache.getIfPresent(file.remote.uniqueId);
        if (downloadItem != null) {
            logger.info("Download Item Complete");
            videoItemCache.invalidate(file.remote.uniqueId);
        }
    }


    public void updateDownloadStatus(TdApi.File file) {
        DownloadItem item = videoItemCache.getIfPresent(file.remote.uniqueId);
        if (item != null) {
            item.setTdFile(file);
        }
        if (file.local.isDownloadingCompleted) {
            logger.info("file download completed: {}", LoggerUtils.shrinkText(file.toString(), 1000));
            downloadComplete(file);
        }
    }

    public Map<String, DownloadItem> getDownloadItems() {
        List<DownloadItem> completedItems = videoItemCache.asMap().values().stream().filter(item -> item.getTdFile().local.isDownloadingCompleted).toList();
        for (DownloadItem completedItem : completedItems) {
            videoItemCache.invalidate(completedItem.geId());
        }
        return videoItemCache.asMap();
    }
}
