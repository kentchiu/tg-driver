package com.kent.tg.web;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kent.tg.client.TgFileUtils;
import com.kent.tg.daemon.ChatListener;
import com.kent.tg.domain.Chat;
import com.kent.tg.service.ChatService;
import com.kent.tg.service.impl.DownloadServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Tag(name = "Chat")
@RestController
public class ChatController {

    private final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private ChatService chatService;
    @Autowired
    private ChatListener chatListener;
    @Autowired
    private DownloadServiceImpl downloadService;


    @GetMapping("/chats")
    @Operation(summary = "Get Chat List", description = "Get Chat List")
    List<Map<String, Object>> listChats(
            @Parameter(description = "NOTE: refresh will remove all chat avators image and re-download it. Don't over used refresh flag to frequently")
            @RequestParam(defaultValue = "false", required = false) boolean refresh
    ) {
        if (refresh) {
            this.refreshChats();
        }
        QueryWrapper<Map<String, Object>> wrapper = Wrappers.query();
        wrapper.eq("chat.disabled", false);
        wrapper.in("message.type", List.of("MessagePhoto", "MessageVideo"));
        wrapper.isNull("message.read_at");
        wrapper.isNull("message.delete_at");
        List<Map<String, Object>> results = chatService.findAllWithMessageCount(wrapper);
        return results;
    }

    @PutMapping("/chats")
    @Operation(summary = "Refresh Chat List", description = "Get Chat List will remove all chat profile image and re-download. After re-download, file ")
    Map<String, Object> refreshChats() {
        List<Chat> chats = this.refresh();
        return Map.of("success", true, "chatCount", chats.size());
    }

    List<Chat> refresh() {
        chatListener.fetchMainChatIds();
        chatListener.fetchMainChats();

        Collection<TdApi.Chat> tdChats = chatListener.getChatMap().values();

        removeNotIn(tdChats);
        TgFileUtils.getInstance().deleteProfileHome();

        List<Chat> chatsInDb = chatService.list();

        List<Chat> chats = tdChats.stream().map(c -> {
            Chat chat = chatsInDb.stream().filter(value -> value.getChatId() == c.id).findFirst().orElse(new Chat());
            chat.setChatId(c.id);
            chat.setName(c.title);
            if (c.photo != null) {
                chat.setFileUniqueId(c.photo.small.remote.uniqueId);
                downloadService.downloadPhotoFromChat(c);
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

}
