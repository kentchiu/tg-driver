package com.kent.tg.domain.dto;

public class DownloadDto {

    private Long chatId;
    private Long messageId;
    private Long messageUid;

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getMessageUid() {
        return messageUid;
    }

    public void setMessageUid(Long messageUid) {
        this.messageUid = messageUid;
    }

    @Override
    public String toString() {
        return "DownloadDto{" +
                "chatId=" + chatId +
                ", messageId=" + messageId +
                ", messageUid=" + messageUid +
                '}';
    }
}
