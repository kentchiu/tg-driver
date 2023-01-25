package com.kent.tg.daemon;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kent.tg.client.TgFileUtils;
import com.kent.tg.domain.File;
import com.kent.tg.domain.dto.MessageQueryDto;
import com.kent.tg.service.FileService;
import com.kent.tg.service.MessageService;
import com.kent.tg.service.impl.DownloadServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Check file existing status
 */
@Service
public class FileExistingCheckTask {

    private final Logger logger = LoggerFactory.getLogger(FileExistingCheckTask.class);

    @Autowired
    private FileService fileService;
    @Autowired
    private DownloadServiceImpl downloadService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private AuthListener authListener;

    @Scheduled(cron = "*/10 * * * * *")
    public void updateFileExistFlag() {
        if (!authListener.isReady()) {
            return;
        }
        List<File> missingFile = listVideoRecordsHaveNoFile();
        for (File file : missingFile) {
            logger.warn("Missing File: {}", file);
        }
        if (!missingFile.isEmpty()) {
            List<File> files = missingFile.stream().map(f -> {
                f.setExist(false);
                return f;
            }).collect(Collectors.toList());
            logger.info("Update {} missing files as not exist", files.size());
            fileService.updateBatchById(files);
        } else {
            logger.debug("No missing file");
        }
    }


    @Scheduled(cron = "*/10 * * * * *")
    public void downloadMissingFile() {
        if (!authListener.isReady()) {
            return;
        }
        IPage<Map<String, Object>> result = queryMissingFiles();
        result.getRecords().forEach(each -> {
            try {
                logger.debug("Fetch Message uid:{}", each.get("uid"));
                long messageId = (long) each.get("messageId");
                Map<String, Object> chat = (Map<String, Object>) each.get("chat");
                long chatId = (long) chat.get("chatId");
                downloadService.syncDownloadPhoneFromMessage(messageId, chatId);
            } catch (Exception e) {
                logger.warn("Download file of message fila, message: {}", each);
            }
        });
        var type = "";
    }

    @NotNull
    private IPage<Map<String, Object>> queryMissingFiles() {
        QueryWrapper<Map<String, Object>> wrapper = Wrappers.query();
        MessageQueryDto query = new MessageQueryDto();
        query.setPageSize(1000);
        Page page = query.toPage();
        page.setOptimizeCountSql(false);
        page.setSearchCount(false);
        wrapper.isNull("photo_file.uid");
        wrapper.isNull("message.read_at");
        wrapper.isNull("message.delete_at");
        wrapper.in(query.getTypes().length == 0, "message.type", "MessagePhoto", "MessageVideo");
        Map<String, String> nameMap = Map.of("id", "message.id");
        IPage<Map<String, Object>> result = messageService.findAll(query.toPage(nameMap), wrapper);
        logger.info("{} missing files to be downloaded", result.getTotal());
        return result;
    }


    @Nullable
    private List<File> listVideoRecordsHaveNoFile() {
        // File record marked as exist=true but has no physical file should be updated to exist = false
        Path videoHome = TgFileUtils.getInstance().getPathByName(TgFileUtils.VIDEO_HOME);
        Set<String> fileNames = Arrays.stream(videoHome.toFile().listFiles()).map(f -> f.getName()).collect(Collectors.toSet());
        String[] names = fileNames.toArray(new String[0]);
        logger.debug("{} video files in File System", names.length);
        LambdaQueryWrapper<File> qw = Wrappers.lambdaQuery();
        qw.eq(File::isExist, true);
        qw.likeRight(File::getLocalFilePath, "/downloads/videos/");
        List<File> existInDb = fileService.list(qw);
        logger.debug("{} video files in File Table", existInDb.size());

        List<File> result = existInDb.stream().filter(f -> {
                    String name = StringUtils.substringAfterLast(f.getLocalFilePath(), "/");
                    return !StringUtils.equalsAny(name, names);
                }
        ).toList();
        return result;
    }
}
