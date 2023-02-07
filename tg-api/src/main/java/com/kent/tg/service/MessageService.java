package com.kent.tg.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kent.tg.domain.Message;

import java.util.List;
import java.util.Map;


public interface MessageService extends IService<Message> {

    IPage<Map<String, Object>> findAll(IPage page, Wrapper<Map<String, Object>> queryWrapper);

    int countFindAll(Wrapper<Map<String, Object>> queryWrapper);

    int markAsReadByChatId(long chatId, int lastMessageUid);

    List<Message> findAllMissingPhoto();
}
