package com.kent.tg.web;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kent.base.domain.BaseQuery;
import com.kent.base.exception.ResourceNotFoundException;
import com.kent.tg.client.Telegram;
import com.kent.tg.domain.Chat;
import com.kent.tg.domain.Message;
import com.kent.tg.domain.dto.DownloadDto;
import com.kent.tg.domain.dto.MessageQueryDto;
import com.kent.tg.service.ChatService;
import com.kent.tg.service.MessageService;
import com.kent.tg.service.impl.DownloadServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    private Telegram telegram;
    @Autowired
    private DownloadServiceImpl downloadService;
    @Autowired
    private ChatService chatService;


    @Operation(summary = "Get Message List", description = "Get Message List")
    @GetMapping("/messages")
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
    @GetMapping("/message/videos")
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


    @Operation(summary = "mark chat as read", description = "mark all messages of chat as read")
    @PatchMapping("/messages/mark-as-read-by-chat")
    Map<String, Object> markAsReadByChat(@RequestParam long chatUid, @RequestParam int lastMessageUid) {
        Chat chat = chatService.getById(chatUid);
        int count = messageService.markAsReadByChatId(chat.getChatId(), lastMessageUid);
        return Map.of("affectCount", count);
    }


    @Operation(summary = "download video", description = "download video by chatId and messageId")
    @PostMapping("/videos/download")
    Map<String, Object> downloadVideoByMessage(@RequestBody DownloadDto dto) {
        Long chatId = dto.getChatId();
        Long messageId = dto.getMessageId();
        Map<String, Object> result = new HashMap<>();

        if (dto.getMessageUid() != null) {
            Message msg = messageService.getById(dto.getMessageUid());
            if (msg == null) {
                throw new ResourceNotFoundException(Message.class, dto.getMessageUid().toString());
            }
            chatId = msg.getChatId();
            messageId = msg.getMessageId();
            logger.info("Download video from message: uid:{} charId: {}, messageId: {}", msg.getUid(), chatId, messageId);
        }

        Long finalChatId = chatId;
        Long finalMessageId = messageId;
        result.put("chatId", finalChatId);
        result.put("messageId", finalMessageId);

        logger.info("Download video from message:  charId: {}, messageId: {}", chatId, messageId);
        telegram.sendAsynchronously(new TdApi.GetMessage(finalChatId, finalMessageId), msgResult -> {

            if (msgResult.isError()) {
                logger.error("Video Not Found for charId: {}, messageId: {}", finalChatId, finalMessageId);
            } else {
                downloadService.downloadVideoFromMessage((TdApi.Message) msgResult.get());
            }
        });
        return result;
    }

}
