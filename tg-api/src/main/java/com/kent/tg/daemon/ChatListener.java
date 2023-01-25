package com.kent.tg.daemon;

import com.kent.tg.client.Telegram;
import it.tdlight.client.Result;
import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ChatListener {

    private final Logger logger = LoggerFactory.getLogger(ChatListener.class);
    private final Map<Long, TdApi.Chat> chatMap = new HashMap<>();
    private long[] chatIds;
    @Autowired
    private Telegram telegram;

    public Map<Long, TdApi.Chat> getChatMap() {
        return chatMap;
    }

    public void fetchMainChats() {
        for (long chatId : chatIds) {
            if (!chatMap.containsKey(chatId)) {
                Result<TdApi.Chat> result = this.telegram.sendSynchronously(new TdApi.GetChat(chatId), TdApi.Chat.class, 10);
                if (result.isError()) {
                    logger.warn("Receive an error for GetChats: " + result.getError());
                } else {
                    this.chatMap.put(chatId, result.get());
                }
            }
        }
    }


    public void fetchMainChatIds() {
        TdApi.GetChats request = new TdApi.GetChats(new TdApi.ChatListMain(), 100);
        Result<TdApi.Chats> result = this.telegram.sendSynchronously(request, TdApi.Chats.class, 10);
        if (!result.isError()) {
            this.chatIds = result.get().chatIds;
            logger.info("Fetch MainChatIds: {}", this.chatIds);
        } else {
            logger.warn("Fetch MainChatIds fail: {}", result.error());
        }
    }


    @EventListener
    public void onUpdateChat(TdApi.Object event) {
    }

}
