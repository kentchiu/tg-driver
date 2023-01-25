package com.kent.tg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kent.tg.domain.Message;
import com.kent.tg.domain.MessagePhoto;
import com.kent.tg.domain.MessageVideo;
import com.kent.tg.mapper.MessageVideoMapper;
import com.kent.tg.service.MessagePhotoService;
import com.kent.tg.service.MessageVideoService;
import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;


@Service
public class MessageVideoServiceImpl extends ServiceImpl<MessageVideoMapper, MessageVideo> implements MessageVideoService {

    private final Logger logger = LoggerFactory.getLogger(MessageVideoServiceImpl.class);

    @Autowired
    private MessagePhotoService messagePhotoService;

    @Transactional
    @Override
    public MessageVideo saveVideo(Message message, TdApi.MessageVideo tdMessageVideo) {
        MessageVideo video = new MessageVideo();
        video.setMessageUid(message.getUid());
        video.setCaption(tdMessageVideo.caption.text);

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
            cover.setMessageUid(message.getUid());
            cover.setCaption(tdMessageVideo.caption.text);
            cover.setHeight(tdMessageVideo.video.thumbnail.height);
            cover.setWidth(tdMessageVideo.video.thumbnail.width);
            cover.setFileUniqueId(tdMessageVideo.video.thumbnail.file.remote.uniqueId);
            messagePhotoService.save(cover);

            video.setThumbnailId(cover.getFileUniqueId());
        } else {
            logger.warn("Video has no thumbnail: {}", tdMessageVideo);
        }

        save(video);
        return video;
    }

    /**
     * @param messageUid
     * @return message, maybe null
     */
    @Override
    public Map<String, Object> findOneByMessageUid(int messageUid) {
        return baseMapper.findOneByMessageUid(messageUid);
    }


}




