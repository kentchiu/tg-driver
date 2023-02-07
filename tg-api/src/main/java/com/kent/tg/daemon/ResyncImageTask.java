package com.kent.tg.daemon;


import com.kent.tg.client.Telegram;
import com.kent.tg.domain.Message;
import com.kent.tg.domain.MessagePhoto;
import com.kent.tg.facade.DownloadFacade;
import com.kent.tg.facade.MessageFacade;
import com.kent.tg.service.MessagePhotoService;
import com.kent.tg.service.MessageService;
import it.tdlight.client.Result;
import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Re-sync image file
 * Re-sync image after user remake the photo or video.
 * NOTE: Re-sync is not work well if message type changed. ex: User send a video message and replace the video to photo
 * and vice versa
 */
@Service
public class ResyncImageTask {

    private final Logger logger = LoggerFactory.getLogger(ResyncImageTask.class);
    @Autowired
    private AuthListener authListener;
    @Autowired
    private MessageService messageService;
    @Autowired
    private MessagePhotoService messagePhotoService;
    @Autowired
    private DownloadFacade downloadFacade;
    @Autowired
    private MessageFacade messageFacade;
    @Autowired
    private Telegram telegram;

    @Scheduled(cron = "*/60 * * * * *")
    public void run() {
        if (!authListener.isReady()) {
            return;
        }
        List<Message> messagesNeedResync = messageService.findAllMissingPhoto();
        logger.info("{} message need be resync", messagesNeedResync.size());
        for (Message message : messagesNeedResync) {
            Result<TdApi.Message> tdMessageResult = telegram.getMessage(message.getMessageId(), message.getChatId());
            if (tdMessageResult.isError()) {
                logger.error("No Td Message for {}", message);
            } else {
                TdApi.Message tdMessage = tdMessageResult.get();
                if (tdMessage.content instanceof TdApi.MessagePhoto mp) {
                    resyncMessagePhoto(message, mp);
                }
                if (tdMessage.content instanceof TdApi.MessageVideo mv) {
                    resyncMessageVideo(message, mv);
                }
            }
        }
    }

    private void resyncMessagePhoto(Message message, TdApi.MessagePhoto mp) {
        Long messageUid = message.getUid();
        Optional<MessagePhoto> photoOptional = messagePhotoService.findOneByMessageUid(messageUid);
        if (photoOptional.isPresent()) {
            logger.info("Resync photo of message@{}, date: {}", message.getUid(), message.getDate());
            messageFacade.updatePhoto(mp, photoOptional.get(), messageUid);
            downloadFacade.asyncDownloadPhotoFromMessageContent(mp);
        } else {
            logger.error("MessagePhoto not found: messageUid: {}", messageUid);
        }
    }

    private void resyncMessageVideo(Message message, TdApi.MessageVideo mv) {
        Long messageUid = message.getUid();
        Optional<MessagePhoto> photoOptional = messagePhotoService.findOneByMessageUid(messageUid);
        if (photoOptional.isPresent()) {
            logger.info("Resync thumbnail of message@{}", message.getUid());
            messageFacade.updateThumbnail(mv, photoOptional.get(), messageUid);
            downloadFacade.asyncDownloadPhotoFromMessageContent(mv);
        } else {
            logger.error("MessagePhoto not found: messageUid: {}", messageUid);
        }
    }

}
