package com.kent.tg.domain.dto;

import com.kent.tg.domain.Message;
import it.tdlight.jni.TdApi;

public class DownloadItem {
    private final Message message;
    private final TdApi.Message tdMessage;
    private TdApi.File tdFile;

    public DownloadItem(Message message, TdApi.Message tdMessage) {
        this.message = message;
        this.tdMessage = tdMessage;

        if (tdMessage.content instanceof TdApi.MessageVideo mv) {
            tdFile = mv.video.video;
        }
    }

    public Message getMessage() {
        return message;
    }

    public TdApi.Message getTdMessage() {
        return tdMessage;
    }

    public TdApi.File getTdFile() {
        return tdFile;
    }

    public void setTdFile(TdApi.File tdFile) {
        this.tdFile = tdFile;
    }

    public String geId() {
        return tdFile.remote.uniqueId;
    }

}