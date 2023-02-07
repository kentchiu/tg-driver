package com.kent.tg.domain.dto;

public class DownloadInputDto {

    private long messageUid;

    public long getMessageUid() {
        return messageUid;
    }

    public void setMessageUid(long messageUid) {
        this.messageUid = messageUid;
    }

    @Override
    public String toString() {
        return "QueueInputDto{" +
                "messageUid=" + messageUid +
                '}';
    }
}
