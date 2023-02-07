package com.kent.tg.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

/**
 * @TableName message_video_tag
 */
@Deprecated
@TableName(value = "message_video_tag")
public class MessageVideoTag implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    @TableId(value = "uid", type = IdType.AUTO)
    private Long uid;
    /**
     *
     */
    @TableField(value = "message_video_uid")
    private Long messageVideoUid;
    /**
     *
     */
    @TableField(value = "tag_uid")
    private Long tagUid;

    /**
     *
     */
    public Long getUid() {
        return uid;
    }

    /**
     *
     */
    public void setUid(Long uid) {
        this.uid = uid;
    }

    /**
     *
     */
    public Long getMessageVideoUid() {
        return messageVideoUid;
    }

    /**
     *
     */
    public void setMessageVideoUid(Long messageVideoUid) {
        this.messageVideoUid = messageVideoUid;
    }

    /**
     *
     */
    public Long getTagUid() {
        return tagUid;
    }

    /**
     *
     */
    public void setTagUid(Long tagUid) {
        this.tagUid = tagUid;
    }

    @Override
    public String toString() {
        return "MessageVideoTag{" +
                "uid=" + uid +
                ", messageVideoUid=" + messageVideoUid +
                ", tagUid=" + tagUid +
                '}';
    }
}