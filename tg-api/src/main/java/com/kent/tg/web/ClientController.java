package com.kent.tg.web;

import com.kent.base.exception.ResourceNotFoundException;
import com.kent.tg.client.Telegram;
import com.kent.tg.domain.Message;
import com.kent.tg.service.MessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
    @Autowired
    private Telegram telegram;


    @GetMapping("/client/get-message/by-uid")
    public Map<String, Object> sendGetMessageByUid(@RequestParam Long messageUid) throws ExecutionException, InterruptedException {
        Message msg = messageService.getById(messageUid);
        if (msg == null) {
            throw new ResourceNotFoundException(Message.class, messageUid.toString());
        }
        var chatId = msg.getChatId();
        var messageId = msg.getMessageId();
        var request = new TdApi.GetMessage(chatId, messageId);
        TdApi.Object object = telegram.sendSynchronously(request, TdApi.Object.class, 30).get();
        return Map.of("result", object);
    }


    @GetMapping("/client/get-message")
    public Map<String, Object> sendGetMessage(@RequestParam Long chatId, @RequestParam Long messageId) throws ExecutionException, InterruptedException {
        var request = new TdApi.GetMessage(chatId, messageId);
        TdApi.Object object = telegram.sendSynchronously(request, TdApi.Object.class, 30).get();
        return Map.of("result", object);
    }


    @GetMapping("/client/get-chat")
    public Map<String, Object> sendGetChat(@RequestParam Long chatId) throws ExecutionException, InterruptedException {
        var request = new TdApi.GetChat(chatId);
        TdApi.Object object = telegram.sendSynchronously(request, TdApi.Object.class, 30).get();
        return Map.of("result", object);
    }

    @GetMapping("/client/get-chat-history")
    public Map<String, Object> sendGetMessage(@RequestParam Long chatId, @RequestParam Long fromMessageId, @RequestParam Integer offset, @RequestParam Integer limit) throws ExecutionException, InterruptedException {
        var request = new TdApi.GetChatHistory(chatId, fromMessageId, offset, limit, false);
        TdApi.Object object = telegram.sendSynchronously(request, TdApi.Object.class, 60).get();
        return Map.of("result", object);
    }

    @GetMapping("/client/leave-chat")
    public Map<String, Object> leaveChat(@RequestParam Long chatId) throws ExecutionException, InterruptedException {
        var request = new TdApi.LeaveChat(chatId);
        TdApi.Object object = telegram.sendSynchronously(request, TdApi.Object.class, 60).get();
        return Map.of("result", object);
    }


    @GetMapping("/client/download-file")
    public void downloadFile(int fileId) {
        int priority = 1;
        int offset = 0;
        int limit = 0;
        boolean synchronous = false;
        TdApi.DownloadFile request = new TdApi.DownloadFile(fileId, priority, offset, limit, synchronous);
        telegram.sendAsynchronously(request);
    }

}
