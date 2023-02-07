package com.kent.tg.daemon;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kent.tg.domain.Message;
import com.kent.tg.facade.ChatFacade;
import com.kent.tg.facade.DownloadFacade;
import com.kent.tg.facade.MessageFacade;
import com.kent.tg.service.MessageService;
import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class MessageListener {

    private final Logger logger = LoggerFactory.getLogger(MessageListener.class);
    @Autowired
    private MessageService messageService;
    @Autowired
    private DownloadFacade downloadFacade;
    @Autowired
    private MessageFacade messageFacade;
    @Autowired
    private ChatFacade chatFacade;

    /**
     * 會獲取最後一次收到信息後的所有新信息，包含離線時發出的任何信息
     *
     * @param tdNewMsg
     */
    @EventListener
    public void onNewMessage(TdApi.UpdateNewMessage tdNewMsg) {
        TdApi.Message tdMsg = tdNewMsg.message;

        List<Long> chatIds = chatFacade.getDisabledChatIds();
        if (chatIds.contains(tdMsg.chatId)) {
            logger.trace("Ignore Message {} when chat id = {}", tdMsg.id, tdMsg.chatId);
            return;
        }

        Message message = messageFacade.saveMessage(tdMsg);
        messageFacade.saveMessageContent(message, tdMsg);
        downloadFacade.asyncDownloadPhotoFromMessageContent(tdMsg.content);
    }


    @EventListener
    public void onDeleteMessages(TdApi.UpdateDeleteMessages event) {
        // NOTE: TDLib unloads unneeded messages from memory which fire UpdateDeleteMessages with fromCache = true
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