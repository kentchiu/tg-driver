package com.kent.tg.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * @TableName message
 */
@Schema(description = "员工")
@TableName(value = "message")
public class Message {

    @Schema(description = "主键")
    @TableId(value = "uid", type = IdType.AUTO)
    private Long uid;
    @Schema(description = "Chat ID")
    @TableField(value = "chat_id")
    private Long chatId;
    @Schema(description = "Message ID")
    @TableField(value = "message_id")
    private Long messageId;
    @TableField(value = "date")
    private LocalDateTime date;
    @TableField(value = "media_album_id")
    private Long mediaAlbumId;
    @TableField(value = "message_thread_id")
    private Long messageThreadId;
    @TableField(value = "reply_to_message_id")
    private Long replyToMessageId;
    @TableField(value = "reply_in_chat_id")
    private Long replyInChatId;
    @Schema(description = "Message Type")
    @TableField(value = "type")
    private String type;
    @TableField(value = "read_at")
    private LocalDateTime readAt;
    @TableField(value = "delete_at")
    private LocalDateTime deleteAt;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getMediaAlbumId() {
        return mediaAlbumId;
    }

    public void setMediaAlbumId(Long mediaAlbumId) {
        this.mediaAlbumId = mediaAlbumId;
    }

    public Long getMessageThreadId() {
        return messageThreadId;
    }

    public void setMessageThreadId(Long messageThreadId) {
        this.messageThreadId = messageThreadId;
    }

    public Long getReplyToMessageId() {
        return replyToMessageId;
    }

    public void setReplyToMessageId(Long replyToMessageId) {
        this.replyToMessageId = replyToMessageId;
    }

    public Long getReplyInChatId() {
        return replyInChatId;
    }

    public void setReplyInChatId(Long replyInChatId) {
        this.replyInChatId = replyInChatId;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public LocalDateTime getDeleteAt() {
        return deleteAt;
    }

    public void setDeleteAt(LocalDateTime deleteAt) {
        this.deleteAt = deleteAt;
    }

    @Override
    public String toString() {
        return "Message{" +
                "uid=" + uid +
                ", chatId=" + chatId +
                ", messageId=" + messageId +
                ", date=" + date +
                ", mediaAlbumId=" + mediaAlbumId +
                ", messageThreadId=" + messageThreadId +
                ", replyToMessageId=" + replyToMessageId +
                ", replyInChatId=" + replyInChatId +
                ", type='" + type + '\'' +
                ", readAt=" + readAt +
                ", deleteAt=" + deleteAt +
                '}';
    }

}