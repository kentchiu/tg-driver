package com.kent.tg.web;

import com.kent.base.exception.ApiException;
import com.kent.base.exception.ResourceNotFoundException;
import com.kent.tg.client.TgFileUtils;
import com.kent.tg.domain.File;
import com.kent.tg.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Tag(name = "File")
@RestController
public class FileController {
    private final Logger logger = LoggerFactory.getLogger(FileController.class);
    @Autowired
    private FileService fileService;

    @Operation(summary = "Delete a file", description = "Delete a file")
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


}

