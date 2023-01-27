package com.kent.tg.daemon;

import com.kent.base.domain.DomainUtil;
import com.kent.base.exception.ResourceNotFoundException;
import com.kent.tg.client.Telegram;
import com.kent.tg.client.TgFileUtils;
import com.kent.tg.domain.Message;
import com.kent.tg.service.MessageService;
import com.kent.tg.service.MessageVideoService;
import com.kent.tg.service.impl.DownloadServiceImpl;
import it.tdlight.jni.TdApi;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VideoDownloadTask {

    private final Logger logger = LoggerFactory.getLogger(VideoDownloadTask.class);
    private final int queueSize = 3;
    private final int downloadSize = 10;
    private final List<Map<String, Object>> messages = new ArrayList<>();
    @Autowired
    private MessageService messageService;
    @Autowired
    private DownloadServiceImpl downloadService;
    @Autowired
    private MessageVideoService messageVideoService;
    @Autowired
    private Telegram telegram;

    @Async
    @Scheduled(cron = "0 0/1 * * * * ") // every 1 minute
    public void download() {
        logger.info("Start auto download task, ({}) tasks", messages.size());
        // remove if file exist
        messages.stream().filter(msg -> msg.containsKey("localFilePath")).map(msg -> {
                    logger.info("File already downloaded : {} -> {}", msg.get("fileUniqueId"), msg.get("localFilePath"));
                    return msg.get("fileUniqueId").toString();
                }
        ).collect(Collectors.toSet()).forEach(fileUniqueId -> this.removeFromQueueByFileUniqueId(fileUniqueId));
        int remainds = queueSize;

        Path videoHome = TgFileUtils.getInstance().getPathByName(TgFileUtils.VIDEO_HOME);
        File[] allVideoFiles = videoHome.toFile().listFiles();

        List<String> completes = Arrays.stream(allVideoFiles).map(val -> val.getName()).toList();
        List<Map<String, Object>> toBeDownloads = messages.stream().filter(val -> {
            String localFilePath = (String) val.get("localFilePath");
            if (StringUtils.isNotBlank(localFilePath)) {
                String fileName = StringUtils.substringAfter(localFilePath, "/downloads/videos/");
                return !completes.contains(fileName);
            }
            return true;
        }).toList();

        for (Map<String, Object> message : toBeDownloads) {
            String localFilePath = (String) message.get("localFilePath");
            Long messageUid = (Long) message.get("messageUid");
            logger.info("To be downloaded: {}", message);
            if (StringUtils.isBlank(localFilePath)) {
                download(messageUid);
                remainds--;
            } else {
                Path filePath = TgFileUtils.getInstance().getFileByLocalFilePath(localFilePath);
                if (!Files.exists(filePath)) {
                    download(messageUid);
                    remainds--;
                }
            }
            if (remainds <= 0) {
                logger.info("Exit download queue({}) by reach max downloading request ({})", toBeDownloads.size(), queueSize);
                break;
            }
        }
        logger.info("End auto download task, ({}) tasks", messages.size());
    }


    private void download(long messageUid) {
        Message msg = messageService.getById(messageUid);

        if (msg == null) {
            throw new ResourceNotFoundException(Message.class, Long.toString(messageUid));
        }
        var chatId = msg.getChatId();
        var messageId = msg.getMessageId();

        telegram.sendAsynchronously(new TdApi.GetMessage(chatId, messageId), tgMsgResult -> {
            logger.info("--->messageUid", messageUid);
            if (tgMsgResult.isError()) {
                logger.error("Video Not Found - charId: {}, messageId: {}, remove from Queue", chatId, messageId);
                removeFromQueueMessageUid(messageUid);
            } else {
                TdApi.Message tgMsg = (TdApi.Message) tgMsgResult.get();
                if (tgMsg.content instanceof TdApi.MessageVideo mv) {
                    logger.info("Download video from messageUid: {} - {}", msg.getUid(), DomainUtil.convertPojoToMap(mv.video.video));
                }
                downloadService.downloadVideoFromMessage(tgMsg);
            }
        });
    }

    public Map<String, Object> addToQueue(int messageUid) {
        Map<String, Object> message = messageVideoService.findOneByMessageUid(messageUid);
        if (message == null) {
            logger.warn("Message@{} not found", messageUid);
        } else {
            messages.add(message);
            logger.info("Add message@{} to download queue, fileName: {}, localFilePath: {}", messageUid, message.get("fileName"), message.get("localFilePath"));
        }
        return message;
    }

    public List<Map<String, Object>> getMessages() {
        return messages;
    }

    public void removeFromQueueByFileUniqueId(String uniqueId) {
        messages.removeIf(m -> {
            String fileUniqueId = (String) m.get("fileUniqueId");
            return StringUtils.equals(uniqueId, fileUniqueId);
        });
    }

    public void removeFromQueueMessageUid(long messageUid) {
        messages.removeIf(m -> {
            long uid = (long) m.get("uid");
            return messageUid == uid;
        });
    }

}



