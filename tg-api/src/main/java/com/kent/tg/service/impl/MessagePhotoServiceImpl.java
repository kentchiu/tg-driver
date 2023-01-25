package com.kent.tg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kent.tg.domain.Message;
import com.kent.tg.domain.MessagePhoto;
import com.kent.tg.mapper.MessagePhotoMapper;
import com.kent.tg.service.MessagePhotoService;
import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class MessagePhotoServiceImpl extends ServiceImpl<MessagePhotoMapper, MessagePhoto> implements MessagePhotoService {

    private final Logger logger = LoggerFactory.getLogger(MessagePhotoServiceImpl.class);


    @Transactional
    @Override
    public MessagePhoto savePhoto(Message message, TdApi.MessagePhoto tdMessagePhoto) {
        MessagePhoto photo = new MessagePhoto();
        photo.setMessageUid(message.getUid());
        photo.setCaption(tdMessagePhoto.caption.text);

        TdApi.PhotoSize ps = getLastPhotoSize(tdMessagePhoto);
        photo.setFileUniqueId(ps.photo.remote.uniqueId);
        photo.setWidth(ps.width);
        photo.setHeight(ps.height);
        save(photo);
        return photo;
    }

    /**
     * MessagePhoto 圖片"可能"會有多個尺寸， 取最大的（最後面的）
     *
     * @param tdMessagePhoto
     * @return
     */
    @Override
    public TdApi.PhotoSize getLastPhotoSize(TdApi.MessagePhoto tdMessagePhoto) {
        return tdMessagePhoto.photo.sizes[tdMessagePhoto.photo.sizes.length - 1];
    }
}




