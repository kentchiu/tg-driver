package com.kent.tg.web;

import com.kent.tg.domain.dto.DownloadInputDto;
import com.kent.tg.facade.DownloadFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Video Download Task", description = "Only works for video type file download, and download in asynchronous")
@RestController
public class DownloadController {

    @Autowired
    private DownloadFacade downloadFacade;

    @Operation(summary = "List all downloading item", description = "List latest download status")
    @GetMapping("/downloads")
    List<Map<String, Object>> listDownloadItem() {
        return downloadFacade.getDownloadItems().values().stream().map(item -> {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("messageUid", item.getMessage().getUid());
            result.put("fileId", item.getTdFile().id);
            result.put("size", item.getTdFile().size);
            result.put("downloadedSize", item.getTdFile().local.downloadedSize);
            return result;
        }).toList();
    }

    @Operation(summary = "add file to download queue", description = "Download video file")
    @PostMapping("/downloads")
    @ResponseStatus(HttpStatus.CREATED)
    void addDownloadItem(@RequestBody DownloadInputDto dto) {
        downloadFacade.addDownloadItem(dto.getMessageUid());
    }


}
