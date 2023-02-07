package com.kent.tg.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

/**
 * @TableName ban_rule
 */
@TableName(value = "ban_rule")
public class BanRule implements Serializable {
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
    @TableField(value = "rule")
    private String rule;


    @TableField(value = "property")
    private String property;

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
    public String getRule() {
        return rule;
    }

    /**
     *
     */
    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    @Override
    public String toString() {
        return "BanRule{" +
                "uid=" + uid +
                ", rule='" + rule + '\'' +
                ", property='" + property + '\'' +
                '}';
    }
}