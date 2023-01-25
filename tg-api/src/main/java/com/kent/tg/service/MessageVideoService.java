package com.kent.tg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kent.tg.domain.Message;
import com.kent.tg.domain.MessageVideo;
import it.tdlight.jni.TdApi;

import java.util.Map;


public interface MessageVideoService extends IService<MessageVideo> {

    MessageVideo saveVideo(Message message, TdApi.MessageVideo tdMessageVideo);

    Map<String, Object> findOneByMessageUid(int messageUid);


}
