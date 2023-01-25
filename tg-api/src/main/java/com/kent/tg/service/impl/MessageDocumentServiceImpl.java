package com.kent.tg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kent.tg.domain.Message;
import com.kent.tg.domain.MessageDocument;
import com.kent.tg.mapper.MessageDocumentMapper;
import com.kent.tg.service.MessageDocumentService;
import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class MessageDocumentServiceImpl extends ServiceImpl<MessageDocumentMapper, MessageDocument> implements MessageDocumentService {
    private final Logger logger = LoggerFactory.getLogger(MessageDocumentServiceImpl.class);

    @Override
    @Transactional
    public MessageDocument saveDocument(Message message, TdApi.MessageDocument tdMessageDocument) {
        MessageDocument doc = new MessageDocument();

        doc.setMessageUid(message.getUid());
        doc.setCaption(tdMessageDocument.caption.text);

        doc.setFileName(tdMessageDocument.document.fileName);
        doc.setMimeType(tdMessageDocument.document.mimeType);
        doc.setFileUniqueId(tdMessageDocument.document.document.remote.uniqueId);

        save(doc);
        return doc;
    }
}




