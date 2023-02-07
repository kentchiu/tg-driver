package com.kent.tg.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

/**
 * @TableName tag
 */
@TableName(value = "tag")
public class Tag implements Serializable {
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
    @TableField(value = "name")
    private String name;
    /**
     *
     */
    @TableField(value = "description")
    private String description;

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
    public String getName() {
        return name;
    }

    /**
     *
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     */
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}