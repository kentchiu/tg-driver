package com.kent.tg.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @TableName message_video
 */
@TableName(value = "message_video")
public class MessageVideo {

    @TableId(value = "uid", type = IdType.AUTO)
    private Long uid;
    @TableField(value = "message_uid")
    private Long messageUid;
    @TableField(value = "caption")
    private String caption;
    @TableField(value = "width")
    private Integer width;
    @TableField(value = "height")
    private Integer height;
    @TableField(value = "duration")
    private Integer duration;
    @TableField(value = "file_name")
    private String fileName;
    @TableField(value = "mime_type")
    private String mimeType;
    @TableField(value = "supports_streaming")
    private Boolean supportsStreaming;
    @TableField(value = "file_unique_id")
    private String fileUniqueId;
    @TableField(value = "thumbnail_id")
    private String thumbnailId;
    @TableField(value = "file_size")
    private Long fileSize;


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

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
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


    public Boolean getSupportsStreaming() {
        return supportsStreaming;
    }

    public void setSupportsStreaming(Boolean supportsStreaming) {
        this.supportsStreaming = supportsStreaming;
    }

    public String getFileUniqueId() {
        return fileUniqueId;
    }

    public void setFileUniqueId(String fileUniqueId) {
        this.fileUniqueId = fileUniqueId;
    }

    public String getThumbnailId() {
        return thumbnailId;
    }

    public void setThumbnailId(String thumbnailId) {
        this.thumbnailId = thumbnailId;
    }


    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
}