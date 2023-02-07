package com.kent.tg.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;


/**
 * @TableName file
 */
@TableName(value = "file")
public class File {

    public static int MEGA_BYTES = 1_000_000;

    @TableId(value = "uid", type = IdType.AUTO)
    private Long uid;

    @TableField(value = "file_size")
    private Long fileSize;

    @TableField(value = "file_unique_id")
    private String fileUniqueId;

    @TableField(value = "local_file_path")
    private String localFilePath;

    @TableField(value = "last_modified", fill = FieldFill.INSERT_UPDATE)
    private Date lastModified;

    @TableField(value = "exist")
    private boolean exist;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileUniqueId() {
        return fileUniqueId;
    }

    public void setFileUniqueId(String fileUniqueId) {
        this.fileUniqueId = fileUniqueId;
    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public boolean isExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }

    @Override
    public String toString() {
        return "File{" +
                "uid=" + uid +
                ", fileSize=" + fileSize +
                ", fileUniqueId='" + fileUniqueId + '\'' +
                ", localFilePath='" + localFilePath + '\'' +
                ", lastModified=" + lastModified +
                ", exist=" + exist +
                '}';
    }


}