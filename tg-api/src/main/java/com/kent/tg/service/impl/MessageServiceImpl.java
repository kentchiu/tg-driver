package com.kent.tg.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kent.tg.domain.Message;
import com.kent.tg.mapper.MessageMapper;
import com.kent.tg.service.MessageService;
import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;


@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {
    private final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    @Override
    public IPage<Map<String, Object>> findAll(IPage page, Wrapper<Map<String, Object>> queryWrapper) {
        return baseMapper.findAll(page, queryWrapper);
    }

    @Override
    public int countFindAll(Wrapper<Map<String, Object>> queryWrapper) {
        return baseMapper.countFindAll(queryWrapper);
    }


    @Transactional
    @Override
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
        save(message);
        return message;
    }

    @Override
    public int markAsReadByChatId(long chatId, int lastMessageUid) {
        LambdaUpdateWrapper<Message> wrapper = Wrappers.lambdaUpdate(Message.class);
        wrapper.set(Message::getReadAt, LocalDateTime.now());
        // 0 for all chatId
        wrapper.eq(chatId != 0, Message::getChatId, chatId);
        wrapper.le(Message::getUid, lastMessageUid);
        wrapper.isNull(Message::getDeleteAt);
        wrapper.isNull(Message::getReadAt);
        wrapper.in(Message::getType, "MessageVideo", "MessagePhoto");
        return getBaseMapper().update(null, wrapper);
    }


}




