package com.kent.tg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kent.tg.domain.Message;
import com.kent.tg.domain.MessagePhoto;
import it.tdlight.jni.TdApi;


public interface MessagePhotoService extends IService<MessagePhoto> {

    MessagePhoto savePhoto(Message message, TdApi.MessagePhoto tdMessagePhoto);

    TdApi.PhotoSize getLastPhotoSize(TdApi.MessagePhoto tdMessagePhoto);
}
