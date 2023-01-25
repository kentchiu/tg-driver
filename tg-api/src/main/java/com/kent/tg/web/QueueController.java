package com.kent.tg.web;

import com.kent.tg.daemon.VideoDownloadTask;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Queue")
@RestController
public class QueueController {
    @Autowired
    private VideoDownloadTask downloadTask;


    @Operation(summary = "list message in queue", description = "message in queue")
    @GetMapping("/queue")
    List<Map<String, Object>> listQueue() {
        return downloadTask.getMessages();
    }

    @Operation(summary = "add file to download queue", description = "Message in queue")
    @PostMapping("/queue")
    Map<String, Object> addToQueue(@RequestBody Map<String, Object> body) {
        Integer uid = (Integer) body.get("messageUid");
        if (uid != null) {
            Map<String, Object> results = downloadTask.addToQueue(uid);
            if (results == null) {
                return new HashMap<>();
            } else {
                return results;
            }
        }
        return new HashMap<>();
    }


    @Operation(summary = "remove message in queue", description = "message in queue")
    @DeleteMapping("/queue")
    void removeFromQueue(@RequestParam String fileUniqueId) {
        downloadTask.removeFormQueue(fileUniqueId);
    }

}
