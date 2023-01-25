package com.kent.tg.domain.dto;

import com.kent.base.domain.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

/**
 * - includes: 包含的 tags
 * - excludes： 不包含的 tags
 * <p>
 * includes 的優先權大於 excludes
 */
@Schema(description = "Video Message Query DTO")
public class VideoQueryDto extends BaseQuery {

    private String[] includes;
    private String[] excludes;

    public String[] getIncludes() {
        return includes;
    }

    public void setIncludes(String[] includes) {
        this.includes = includes;
    }

    public String[] getExcludes() {
        return excludes;
    }

    public void setExcludes(String[] excludes) {
        this.excludes = excludes;
    }

    @Override
    public String toString() {
        return "VideoQueryDto{" +
                "includes=" + Arrays.toString(includes) +
                ", excludes=" + Arrays.toString(excludes) +
                '}';
    }

    //    private Long id;
//    private String chatId;
//    private String keyword;
//    private String chatName;
//    private Boolean showHistory = false;
//    private Boolean showDeleted = false;
//
//    private Long beforeMessageId;
//
//
//    @Options(value = {"MessageText", "MessagePhoto", "MessageVideo", "MessageDocument"})
//    @Schema(description = "Message 類型", allowableValues = "MessageText,MessagePhoto,MessageVideo,MessageDocument")
//    private String[] types = new String[0];
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String[] getTypes() {
//        return types;
//    }
//
//    public void setTypes(String[] types) {
//        this.types = types;
//    }
//
//    public String getChatId() {
//        return chatId;
//    }
//
//    public void setChatId(String chatId) {
//        this.chatId = chatId;
//    }
//
//    public String getKeyword() {
//        return keyword;
//    }
//
//    public void setKeyword(String keyword) {
//        this.keyword = keyword;
//    }
//
//    public String getChatName() {
//        return chatName;
//    }
//
//    public void setChatName(String chatName) {
//        this.chatName = chatName;
//    }
//
//    public Boolean getShowHistory() {
//        return showHistory;
//    }
//
//    public void setShowHistory(Boolean showHistory) {
//        this.showHistory = showHistory;
//    }
//
//    public Boolean getShowDeleted() {
//        return showDeleted;
//    }
//
//    public void setShowDeleted(Boolean showDeleted) {
//        this.showDeleted = showDeleted;
//    }
//
//    public Long getBeforeMessageId() {
//        return beforeMessageId;
//    }
//
//    public void setBeforeMessageId(Long beforeMessageId) {
//        this.beforeMessageId = beforeMessageId;
//    }
}
