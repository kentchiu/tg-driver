package com.kent.tg.daemon;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kent.tg.client.LoggerUtils;
import com.kent.tg.domain.Chat;
import com.kent.tg.domain.Message;
import com.kent.tg.service.*;
import com.kent.tg.service.impl.DownloadServiceImpl;
import it.tdlight.jni.TdApi;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MessageListener {

    private final Logger logger = LoggerFactory.getLogger(MessageListener.class);

    @Autowired
    private MessageService messageService;
    @Autowired
    private MessageTextService messageTextService;
    @Autowired
    private MessagePhotoService messagePhotoService;
    @Autowired
    private MessageVideoService messageVideoService;
    @Autowired
    private MessageDocumentService messageDocumentService;
    @Autowired
    private DownloadServiceImpl downloadService;
    @Autowired
    private ChatService chatService;
    private List<Long> disableChatIds = new ArrayList<>();
    private Date cachedTime;


    /**
     * 會獲取最後一次收到信息後的所有新信息，包含離線時發出的任何信息
     *
     * @param message
     */
    @EventListener
    public void onNewMessage(TdApi.UpdateNewMessage message) {
        TdApi.Message msg = message.message;

        List<Long> chatIds = getDisabledChatIds();
        if (chatIds.contains(msg.chatId)) {
            logger.trace("Ignore Message {} when chat id = {}", msg.id, msg.chatId);
            return;
        }

        saveMessage(msg);

        if (msg.content instanceof TdApi.MessagePhoto || msg.content instanceof TdApi.MessageVideo) {
            downloadService.downloadPhotoFromMessage(msg);
        }

    }

    private List<Long> getDisabledChatIds() {
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

    public void saveMessage(TdApi.Message msg) {
        Message message = messageService.saveMessage(msg);
        TdApi.MessageContent content = msg.content;
        if (content instanceof TdApi.MessageText mt) {
            messageTextService.saveText(message, mt);
        } else if (content instanceof TdApi.MessagePhoto mp) {
            messagePhotoService.savePhoto(message, mp);
        } else if (content instanceof TdApi.MessageVideo mv) {
            messageVideoService.saveVideo(message, mv);
        } else if (content instanceof TdApi.MessageDocument md) {
            messageDocumentService.saveDocument(message, md);
        }
        LoggerUtils.logMessage(logger, msg);
    }

    private String shrinkText(String text, int maxWidth) {
        return StringUtils.abbreviate(text.replaceAll("\n", " "), maxWidth);
    }

    @EventListener
    public void onDeleteMessages(TdApi.UpdateDeleteMessages event) {
        // NOTE: TDLib unloads unneeded messages from memory which fire UndateDeleteMessages with fromCache = true
        if (event.fromCache) {
            return;
        }
        long[] messageIds = event.messageIds;
        long chatId = event.chatId;
        LambdaUpdateWrapper<Message> wrapper = Wrappers.lambdaUpdate(Message.class);
        wrapper.eq(Message::getChatId, chatId);
        List<Long> ids = Arrays.stream(messageIds).boxed().toList();
        wrapper.in(Message::getMessageId, ids);
        wrapper.set(Message::getDeleteAt, new Date());
        boolean update = messageService.update(wrapper);
        if (update) {
            logger.debug("Delete Messages: chatId: {} , messageIds: {}", chatId, messageIds);
        }
    }


}