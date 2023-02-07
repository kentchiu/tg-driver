package com.kent.tg.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kent.tg.domain.Message;
import com.kent.tg.mapper.MessageMapper;
import com.kent.tg.service.MessageService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Override
    public IPage<Map<String, Object>> findAll(IPage page, Wrapper<Map<String, Object>> queryWrapper) {
        return baseMapper.findAll(page, queryWrapper);
    }

    @Override
    public int countFindAll(Wrapper<Map<String, Object>> queryWrapper) {
        return baseMapper.countFindAll(queryWrapper);
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

    @Override
    public List<Message> findAllMissingPhoto() {
        return getBaseMapper().findAllMissingPhoto();
    }


}




