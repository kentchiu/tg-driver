package com.kent.tg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kent.tg.domain.Message;
import com.kent.tg.domain.MessageText;
import it.tdlight.jni.TdApi;


public interface MessageTextService extends IService<MessageText> {

    MessageText saveText(Message message, TdApi.MessageText tdMessageText);

}
