package com.kent.tg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kent.tg.domain.Message;
import com.kent.tg.domain.MessageText;
import com.kent.tg.mapper.MessageTextMapper;
import com.kent.tg.service.MessageTextService;
import it.tdlight.jni.TdApi;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class MessageTextServiceImpl extends ServiceImpl<MessageTextMapper, MessageText>
        implements MessageTextService {
    private final Logger logger = LoggerFactory.getLogger(MessageTextServiceImpl.class);

    @Transactional
    @Override
    public MessageText saveText(Message message, TdApi.MessageText tdMessageText) {
        MessageText text = new MessageText();
        text.setMessageUid(message.getUid());
        String abbreviate = StringUtils.abbreviate(tdMessageText.text.text, 2000);
        text.setText(abbreviate);
        save(text);
        return text;
    }

}




