package com.kent.tg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kent.tg.domain.Message;
import com.kent.tg.domain.MessageDocument;
import it.tdlight.jni.TdApi;

public interface MessageDocumentService extends IService<MessageDocument> {

    MessageDocument saveDocument(Message message, TdApi.MessageDocument tdMessageDocument);

}
