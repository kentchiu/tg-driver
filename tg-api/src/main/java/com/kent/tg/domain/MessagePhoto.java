package com.kent.tg.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @TableName message_photo
 */
@TableName(value = "message_photo")
public class MessagePhoto {
    @TableId(value = "uid", type = IdType.AUTO)
    private Long uid;
    @TableField(value = "message_uid")
    private Long messageUid;
    @TableField(value = "caption")
    private String caption;
    @TableField(value = "file_unique_id")
    private String fileUniqueId;
    @TableField(value = "width")
    private Integer width;
    @TableField(value = "height")
    private Integer height;

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

    public String getFileUniqueId() {
        return fileUniqueId;
    }

    public void setFileUniqueId(String fileUniqueId) {
        this.fileUniqueId = fileUniqueId;
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


}