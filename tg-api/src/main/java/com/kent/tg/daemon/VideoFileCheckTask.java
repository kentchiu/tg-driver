package com.kent.tg.daemon;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kent.tg.client.TgFileUtils;
import com.kent.tg.domain.File;
import com.kent.tg.service.FileService;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Check file existing status
 */
@Service
public class VideoFileCheckTask {

    private final Logger logger = LoggerFactory.getLogger(VideoFileCheckTask.class);
    @Autowired
    private FileService fileService;
    @Autowired
    private AuthListener authListener;

    @Scheduled(cron = "*/60 * * * * *")
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

    @Nullable
    private List<File> listVideoRecordsHaveNoFile() {
        // File record marked as exist=true but has no physical file should be updated to exist = false
        java.io.File[] files = TgFileUtils.getInstance().listAllVideoFiles();
        Set<String> fileNames = Arrays.stream(files).map(f -> f.getName()).collect(Collectors.toSet());
        String[] names = fileNames.toArray(new String[0]);
        logger.debug("{} video files in File System", names.length);
        LambdaQueryWrapper<File> qw = Wrappers.lambdaQuery();
        qw.eq(File::isExist, true);
        qw.likeRight(File::getLocalFilePath, "downloads/videos/");
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
