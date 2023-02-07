package com.kent.tg.web;

import com.kent.tg.client.Telegram;
import com.kent.tg.domain.Message;
import com.kent.tg.service.MessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.tdlight.client.Result;
import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Tag(name = "Telegram Server")
@RestController
public class ClientController {

    private final Logger logger = LoggerFactory.getLogger(ClientController.class);
    @Autowired
    private MessageService messageService;
    /*
     * To make telegram API call as simply as possible.DO NOT use other service expect MessageService.
     * Simplicity means call API without any side effect to database.
     */
    @Autowired
    private Telegram telegram;


    @GetMapping(value = "/client/messages/{uid}")
    public Map<String, Object> getMessageByUid(@PathVariable long uid) throws ExecutionException, InterruptedException {
        Message msg = messageService.getById(uid);
        Result<TdApi.Message> result = telegram.getMessage(msg.getMessageId(), msg.getChatId());
        if (result.isError()) {
            return Map.of("error", result.error());
        } else {
            return Map.of("data", result.get());
        }
    }


    @GetMapping(value = "/client/messages")
    public Result<TdApi.Message> getMessage(@RequestParam Long chatId, @RequestParam Long messageId) {
        return telegram.getMessage(messageId, chatId);
    }


    @GetMapping("/client/chats/{chatId}")
    public Result<TdApi.Chat> getGetChat(@PathVariable Long chatId) {
        return telegram.getChat(chatId);
    }

    @GetMapping("/client/file/{fileId}")
    public void getGetFile(@PathVariable int fileId) {
        telegram.asyncDownloadByFileId(fileId);
    }

}
