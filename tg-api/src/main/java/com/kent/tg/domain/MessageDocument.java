package com.kent.tg.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @TableName message_document
 */
@TableName(value = "message_document")
public class MessageDocument {
    @TableId(value = "uid", type = IdType.AUTO)
    private Long uid;

    @TableField(value = "message_uid")
    private Long messageUid;

    @TableField(value = "caption")
    private String caption;

    @TableField(value = "file_name")
    private String fileName;

    @TableField(value = "mime_type")
    private String mimeType;

    @TableField(value = "file_unique_id")
    private String fileUniqueId;


    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Long getMessageUid() {
        return messageUid;
    }

    public void setMessageUid(Long messageUid) {
        this.messageUid = messageUid;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getFileUniqueId() {
        return fileUniqueId;
    }

    public void setFileUniqueId(String fileUniqueId) {
        this.fileUniqueId = fileUniqueId;
    }

    @Override
    public String toString() {
        return "MessageDocument{" +
                "uid=" + uid +
                ", messageUid=" + messageUid +
                ", caption='" + caption + '\'' +
                ", fileName='" + fileName + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", fileUniqueId='" + fileUniqueId + '\'' +
                '}';
    }
}