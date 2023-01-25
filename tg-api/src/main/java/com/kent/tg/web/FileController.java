package com.kent.tg.web;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kent.base.exception.ApiException;
import com.kent.base.exception.ResourceNotFoundException;
import com.kent.tg.client.TgFileUtils;
import com.kent.tg.daemon.UpdateFileListener;
import com.kent.tg.domain.File;
import com.kent.tg.domain.dto.MessageQueryDto;
import com.kent.tg.service.FileService;
import com.kent.tg.service.MessageService;
import com.kent.tg.service.impl.DownloadServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Tag(name = "File")
@RestController
public class FileController {
    private final Logger logger = LoggerFactory.getLogger(FileController.class);
    @Autowired
    private UpdateFileListener updateFileListener;
    @Autowired
    private FileService fileService;
    @Autowired
    private DownloadServiceImpl downloadService;
    @Autowired
    private MessageService messageService;

    @Operation(summary = "List Downloading Files", description = "List Downloading Files")
    @GetMapping("files/downloading")
    Map<String, TdApi.File> listDownloadingFiles() {
        Map<String, TdApi.File> files = updateFileListener.getDownloadingFiles();
        System.out.println("================================");
        for (TdApi.File file : files.values()) {
            System.out.printf("%s : %d / %d - %s%n", file.local.path, file.local.downloadedSize, file.size, file.remote.uniqueId);
        }
        System.out.println("================================");
        return files;
    }

    @Operation(summary = "Delete a file", description = "Delete a  file")
    @DeleteMapping("files/{uid}")
    Map<String, Object> deleteVideoFile(@PathVariable Long uid) {
        File file = fileService.getById(uid);
        if (file == null) {
            throw new ResourceNotFoundException(File.class, uid.toString());
        }
        Path path = TgFileUtils.getInstance().getFileByLocalFilePath(file.getLocalFilePath());
        try {
            if (!Files.exists(path)) {
                logger.warn("Path not exist: {}", path.toAbsolutePath());
                throw new ResourceNotFoundException("Path", path.toAbsolutePath().toString());
            } else {
                try {
                    Files.delete(path);
                    logger.info("Delete file video file [{}]", path);
                    file.setExist(false);
                    fileService.updateById(file);
                } catch (IOException e) {
                    logger.error("Delete Video File Fail", e);
                    throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Delete File Fail: " + path);
                }
            }
        } catch (ApiException e) {
            logger.error("Delete video file fail: " + path.toString(), e);
            return Map.of("success", false, "reason", e.getMessage());
        }
        logger.info("Delete video file: {}", path);
        return Map.of("success", true, "path", path);
    }

    @Operation(summary = "Download Photo File", description = "Download Photo File")
    @PostMapping("photos/{fileUniqueId}")
    Map<String, Object> downloadVideoFile(@PathVariable String fileUniqueId) {
        MessageQueryDto query = new MessageQueryDto();
        Page page = query.toPage();
        QueryWrapper<Map<String, Object>> wrapper = Wrappers.query();
        page.setOptimizeCountSql(false);
        page.setSearchCount(false);
        wrapper.eq(query.getChatUid() != null, "chat.uid", query.getChatUid());
        wrapper.isNull("message.read_at");
        wrapper.isNull("message.delete_at");
        wrapper.in(query.getTypes().length == 0, "message.type", "MessagePhoto", "MessageVideo");
        wrapper.eq("photo.file_unique_id", fileUniqueId);
        Map<String, String> nameMap = Map.of("id", "message.id");
        List<Map<String, Object>> messages = messageService.findAll(query.toPage(nameMap), wrapper).getRecords();
        messages.forEach(each -> {
            Long messageId = (Long) each.get("messageId");
            Map<String, Object> chat = (Map<String, Object>) each.get("chat");
            downloadService.syncDownloadPhoneFromMessage(messageId, (Long) chat.get("chatId"));
        });
        return Map.of("fileUniqueId", fileUniqueId);
    }
}

