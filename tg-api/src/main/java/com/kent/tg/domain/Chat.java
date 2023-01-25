package com.kent.tg.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @TableName chat
 */
@TableName(value = "chat")
public class Chat {
    @TableId(value = "uid", type = IdType.AUTO)
    private Long uid;

    @TableField(value = "disabled")
    private Boolean disabled;

    @TableField(value = "chat_id")
    private Long chatId;

    @TableField(value = "name")
    private String name;

    @TableField(value = "file_unique_id")
    private String fileUniqueId;

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }


    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileUniqueId() {
        return fileUniqueId;
    }

    public void setFileUniqueId(String fileUniqueId) {
        this.fileUniqueId = fileUniqueId;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + uid +
                ", disabled=" + disabled +
                ", chatId=" + chatId +
                ", name='" + name + '\'' +
                ", fileUniqueId='" + fileUniqueId + '\'' +
                '}';
    }

}