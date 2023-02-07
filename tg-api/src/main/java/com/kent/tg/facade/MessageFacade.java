package com.kent.tg.facade;


import com.kent.tg.client.LoggerUtils;
import com.kent.tg.domain.*;
import com.kent.tg.service.*;
import it.tdlight.jni.TdApi;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class MessageFacade {
    public static final int MAX_WIDTH = 990;
    private final Logger logger = LoggerFactory.getLogger(MessageFacade.class);
    @Autowired
    private MessageService messageService;
    @Autowired
    private MessagePhotoService photoService;
    @Autowired
    private MessageVideoService videoService;
    @Autowired
    private MessageDocumentService documentService;
    @Autowired
    private MessageTextService textService;


    public void updatePhoto(TdApi.MessagePhoto mp, MessagePhoto photo, long messageUid) {
        logger.info("Message Photo before update: {}", photo);
        photo.setCaption(LoggerUtils.shrinkText(mp.caption.text, MAX_WIDTH));
        photo.setMessageUid(messageUid);

        TdApi.PhotoSize ps = getLastPhotoSize(mp);
        photo.setFileUniqueId(ps.photo.remote.uniqueId);
        photo.setWidth(ps.width);
        photo.setHeight(ps.height);
        photoService.updateById(photo);
        logger.info("Message Photo after update: {}", photo);
    }


    public void updateThumbnail(TdApi.MessageVideo mp, MessagePhoto photo, long messageUid) {
        logger.info("Message Photo before update: {}", photo);
        photo.setCaption(LoggerUtils.shrinkText(mp.caption.text, MAX_WIDTH));
        photo.setMessageUid(messageUid);

        photo.setFileUniqueId(mp.video.thumbnail.file.remote.uniqueId);
        photo.setWidth(mp.video.width);
        photo.setHeight(mp.video.height);
        photoService.updateById(photo);
        logger.info("Message Photo after update: {}", photo);
    }

    @Transactional
    public void saveMessageContent(Message message, TdApi.Message tdMsg) {
        TdApi.MessageContent content = tdMsg.content;
        if (content instanceof TdApi.MessageText mt) {
            saveText(mt, message.getUid());
        } else if (content instanceof TdApi.MessagePhoto mp) {
            savePhoto(mp, message.getUid());
        } else if (content instanceof TdApi.MessageVideo mv) {
            saveVideo(mv, message.getUid());
        } else if (content instanceof TdApi.MessageDocument md) {
            saveDocument(md, message.getUid());
        }
        LoggerUtils.logMessage(logger, tdMsg);
    }

    private MessageDocument saveDocument(TdApi.MessageDocument tdMessageDocument, long messageUid) {
        MessageDocument doc = new MessageDocument();

        doc.setMessageUid(messageUid);
        doc.setCaption(LoggerUtils.shrinkText(tdMessageDocument.caption.text, MAX_WIDTH));

        doc.setFileName(tdMessageDocument.document.fileName);
        doc.setMimeType(tdMessageDocument.document.mimeType);
        doc.setFileUniqueId(tdMessageDocument.document.document.remote.uniqueId);

        documentService.save(doc);
        return doc;
    }


    private MessagePhoto savePhoto(TdApi.MessagePhoto tdMessagePhoto, long messageUid) {
        MessagePhoto photo = new MessagePhoto();
        photo.setMessageUid(messageUid);
        photo.setCaption(LoggerUtils.shrinkText(tdMessagePhoto.caption.text, MAX_WIDTH));

        TdApi.PhotoSize ps = getLastPhotoSize(tdMessagePhoto);
        photo.setFileUniqueId(ps.photo.remote.uniqueId);
        photo.setWidth(ps.width);
        photo.setHeight(ps.height);
        photoService.save(photo);
        return photo;
    }

    private MessageVideo saveVideo(TdApi.MessageVideo tdMessageVideo, long messageUid) {
        MessageVideo video = new MessageVideo();
        video.setMessageUid(messageUid);
        video.setCaption(LoggerUtils.shrinkText(tdMessageVideo.caption.text, MAX_WIDTH));

        video.setWidth(tdMessageVideo.video.width);
        video.setHeight(tdMessageVideo.video.height);
        video.setDuration(tdMessageVideo.video.duration);
        video.setFileName(tdMessageVideo.video.fileName);
        video.setMimeType(tdMessageVideo.video.mimeType);
        video.setSupportsStreaming(tdMessageVideo.video.supportsStreaming);
        video.setFileSize(tdMessageVideo.video.video.size);

        video.setFileUniqueId(tdMessageVideo.video.video.remote.uniqueId);

        // cover photo for video
        if (tdMessageVideo.video.thumbnail != null) {
            MessagePhoto cover = new MessagePhoto();
            cover.setMessageUid(messageUid);
            cover.setCaption(LoggerUtils.shrinkText(tdMessageVideo.caption.text, MAX_WIDTH));
            cover.setHeight(tdMessageVideo.video.thumbnail.height);
            cover.setWidth(tdMessageVideo.video.thumbnail.width);
            cover.setFileUniqueId(tdMessageVideo.video.thumbnail.file.remote.uniqueId);
            photoService.save(cover);

            video.setThumbnailId(cover.getFileUniqueId());
        } else {
            logger.warn("Video has no thumbnail. video:{} ,tdVideo: {}", video, LoggerUtils.shrinkText(tdMessageVideo.toString(), 10000));
        }

        videoService.save(video);
        return video;
    }


    @Transactional
    public Message saveMessage(TdApi.Message tdMessage) {
        Message message = new Message();
        message.setMessageId(tdMessage.id);
        message.setChatId(tdMessage.chatId);
        message.setMediaAlbumId(tdMessage.mediaAlbumId);
        message.setMessageThreadId(tdMessage.messageThreadId);
        message.setReplyToMessageId(tdMessage.replyToMessageId);
        message.setReplyInChatId(tdMessage.replyInChatId);
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(tdMessage.date, 0, ZoneOffset.ofHours(8)); // config
        message.setDate(localDateTime);
        message.setType(tdMessage.content.getClass().getSimpleName());
        messageService.save(message);
        return message;
    }

    private MessageText saveText(TdApi.MessageText tdMessageText, long messageUid) {
        MessageText text = new MessageText();
        text.setMessageUid(messageUid);
        String abbreviate = StringUtils.abbreviate(tdMessageText.text.text, 2000);
        text.setText(abbreviate);
        textService.save(text);
        return text;
    }

    /**
     * MessagePhoto 圖片"可能"會有多個尺寸， 取最大的（最後面的）
     *
     * @param tdMessagePhoto
     * @return
     */
    public TdApi.PhotoSize getLastPhotoSize(TdApi.MessagePhoto tdMessagePhoto) {
        return tdMessagePhoto.photo.sizes[tdMessagePhoto.photo.sizes.length - 1];
    }

}
