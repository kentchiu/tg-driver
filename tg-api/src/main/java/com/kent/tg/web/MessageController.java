package com.kent.tg.web;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kent.base.domain.BaseQuery;
import com.kent.base.exception.ResourceNotFoundException;
import com.kent.tg.client.Telegram;
import com.kent.tg.client.TgFileUtils;
import com.kent.tg.domain.Chat;
import com.kent.tg.domain.Message;
import com.kent.tg.domain.dto.MessageQueryDto;
import com.kent.tg.facade.DownloadFacade;
import com.kent.tg.service.ChatService;
import com.kent.tg.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.tdlight.client.Result;
import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;


@Tag(name = "Message")
@RestController
public class MessageController {
    private final Logger logger = LoggerFactory.getLogger(MessageController.class);
    @Autowired
    private MessageService messageService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private Telegram telegram;
    @Autowired
    private DownloadFacade downloadFacade;

    @Operation(summary = "Get Message List", description = "Get Message List")
    @GetMapping("messages")
    IPage<Map<String, Object>> listMessages(@Valid MessageQueryDto query) {
        Page page = query.toPage();
        QueryWrapper<Map<String, Object>> wrapper = Wrappers.query();
        page.setOptimizeCountSql(false);
        page.setSearchCount(false);
        if (query.getUids() != null) {
            wrapper.in(query.getUids() != null, "message.uid", query.getUids());
        } else {
            wrapper.eq(query.getChatUid() != null, "chat.uid", query.getChatUid());
            wrapper.isNull("message.read_at");
            wrapper.isNull("message.delete_at");
        }
        wrapper.in(query.getTypes().length == 0, "message.type", "MessagePhoto", "MessageVideo");

        int count = messageService.countFindAll(wrapper);

        Map<String, String> nameMap = Map.of("id", "message.id");
        IPage<Map<String, Object>> result = messageService.findAll(query.toPage(nameMap), wrapper);

        result.setTotal(count);
        return result;
    }

    @Operation(summary = "Get Video Message List", description = "Get Video Message List")
    @GetMapping("message/videos")
    IPage<Map<String, Object>> listVideoMessages(@Valid BaseQuery query) {
        Page page = query.toPage();
        QueryWrapper<Map<String, Object>> wrapper = Wrappers.query();
        page.setOptimizeCountSql(false);
        page.setSearchCount(false);
        wrapper.eq("video_file.exist", true);

        int count = messageService.countFindAll(wrapper);

        Map<String, String> nameMap = Map.of("id", "message.id");
        IPage<Map<String, Object>> result = messageService.findAll(query.toPage(nameMap), wrapper);
        result.setTotal(count);
        return result;
    }

    @Operation(summary = "Mark chat as read", description = "mark all messages of chat as read")
    @PatchMapping("messages/mark-as-read-by-chat")
    Map<String, Object> markAsReadByChat(@RequestParam long chatUid, @RequestParam int lastMessageUid) {
        Chat chat = chatService.getById(chatUid);
        int count = messageService.markAsReadByChatId(chat.getChatId(), lastMessageUid);
        return Map.of("affectCount", count);
    }

    @PatchMapping(value = "messages/{uid}", params = {"action=fixImage"})
    Map<String, Object> fixBrokenImageLink(@PathVariable long uid) {
        Message message = messageService.getById(uid);
        if (message == null) {
            throw new ResourceNotFoundException(Message.class, Long.toString(uid));
        }
        logger.info("Try to fix broker image of message@{}", message);
        Result<TdApi.Message> messageResult = telegram.getMessage(message.getMessageId(), message.getChatId());
        if (messageResult.isError()) {
            throw new ResourceNotFoundException(TdApi.Message.class, "messageId: %d, chatId:%d, messageUid: %d".formatted(message.getMessageId(), message.getChatId(), message.getUid()));
        }
        TdApi.Message tdMsg = messageResult.get();
        Result<TdApi.File> fileResult = downloadFacade.syncDownloadPhotoFromMessageContent(tdMsg.content);
        if (fileResult.isError()) {
            throw new ResourceNotFoundException(TdApi.File.class, "");
        }
        TdApi.File file = fileResult.get();
        Map<String, Object> result = new HashMap<>();
        result.put("fileUniqueId", file.remote.uniqueId);
        result.put("path", TgFileUtils.toRelatePath(file.local.path));
        result.put("isDownloadingCompleted", file.local.isDownloadingCompleted);
        result.put("id", file.id);
        return result;
    }
}
