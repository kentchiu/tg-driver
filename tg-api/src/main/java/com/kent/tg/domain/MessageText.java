package com.kent.tg.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @TableName message_text
 */
@TableName(value = "message_text")
public class MessageText {

    @TableId(value = "uid", type = IdType.AUTO)
    private Long uid;
    @TableField(value = "message_uid")
    private Long messageUid;
    @TableField(value = "text")
    private String text;

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}