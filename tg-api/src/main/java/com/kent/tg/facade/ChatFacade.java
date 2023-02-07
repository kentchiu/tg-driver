package com.kent.tg.facade;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kent.tg.client.Telegram;
import com.kent.tg.client.TgFileUtils;
import com.kent.tg.domain.Chat;
import com.kent.tg.service.ChatService;
import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatFacade {
    private final Logger logger = LoggerFactory.getLogger(ChatFacade.class);
    @Autowired
    private ChatService chatService;
    @Autowired
    private DownloadFacade downloadService;
    @Autowired
    private Telegram telegram;
    private List<Long> disableChatIds = new ArrayList<>();
    private Date cachedTime;

    @Transactional
    public List<Chat> refresh() {
        Collection<TdApi.Chat> tdChats = telegram.listChats().values();

        removeNotIn(tdChats);
        TgFileUtils.getInstance().deleteProfileHome();

        List<Chat> chatsInDb = chatService.list();

        List<Chat> chats = tdChats.stream().map(c -> {
            Chat chat = chatsInDb.stream().filter(value -> value.getChatId() == c.id).findFirst().orElse(new Chat());
            chat.setChatId(c.id);
            chat.setName(c.title);
            if (c.photo != null) {
                chat.setFileUniqueId(c.photo.small.remote.uniqueId);
                downloadService.asyncDownloadPhotoFromChat(c);
            }
            chatService.saveOrUpdate(chat);
            return chat;
        }).collect(Collectors.toList());

        logger.info("Get {} chats from telegram server", chats.size());
        return chats;
    }

    private void removeNotIn(Collection<TdApi.Chat> tdChats) {
        Set<Long> tdChatIds = tdChats.stream().map(c -> c.id).collect(Collectors.toSet());
        LambdaQueryWrapper<Chat> wrapper = Wrappers.lambdaQuery(Chat.class);
        wrapper.notIn(Chat::getChatId, tdChatIds);
        chatService.remove(wrapper);
    }


    public List<Long> getDisabledChatIds() {
        if (cachedTime != null) {
            long timeDiff = new Date().getTime() - cachedTime.getTime();
            boolean expired = timeDiff < (1000 * 60);
            if (expired) {
                return disableChatIds;
            }
        }
        LambdaQueryWrapper<Chat> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Chat::getDisabled, true);

        List<Chat> chats = chatService.list(wrapper);
        cachedTime = new Date();
        disableChatIds = chats.stream().map(chat -> chat.getChatId()).collect(Collectors.toList());
        return disableChatIds;
    }
}
