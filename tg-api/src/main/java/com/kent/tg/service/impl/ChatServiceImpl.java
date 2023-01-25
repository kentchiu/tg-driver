package com.kent.tg.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kent.tg.domain.Chat;
import com.kent.tg.mapper.ChatMapper;
import com.kent.tg.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat> implements ChatService {
    private final Logger logger = LoggerFactory.getLogger(ChatServiceImpl.class);


    @Override
    public List<Map<String, Object>> findAllWithMessageCount(Wrapper<Map<String, Object>> queryWrapper) {
        List<Map<String, Object>> chats = baseMapper.findAll(Wrappers.query());
        List<Map<String, Object>> countMap = baseMapper.countMessage(queryWrapper);

        List<Map<String, Object>> results = chats.stream().map(chatMap -> {
            Optional<Map<String, Object>> matched = countMap.stream().filter(c -> {
                Long uid = (Long) c.get("uid");
                Long uid2 = (Long) chatMap.get("uid");
                return uid == uid2;
            }).findFirst();
            if (matched.isPresent()) {
                long messageCount = (long) matched.get().get("messageCount");
                chatMap.put("messageCount", messageCount);
            }
            return chatMap;
        }).collect(Collectors.toList());

        return results;
    }

}




