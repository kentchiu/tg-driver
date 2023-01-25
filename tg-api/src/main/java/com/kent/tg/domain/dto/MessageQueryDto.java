package com.kent.tg.domain.dto;

import com.kent.base.domain.BaseQuery;
import com.kent.base.domain.Options;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Message Query DTO")
public class MessageQueryDto extends BaseQuery {

    //    private Long id;
    private Long chatUid;
//    private String keyword;
//    private String chatName;
//    private Boolean showHistory = false;
//    private Boolean showDeleted = false;

//    private Long beforeMessageId;

    private Long[] uids;

    @Options(value = {"MessageText", "MessagePhoto", "MessageVideo", "MessageDocument"})
    @Schema(description = "Message 類型", allowableValues = "MessageText,MessagePhoto,MessageVideo,MessageDocument")
    private String[] types = new String[0];

    public Long[] getUids() {
        return uids;
    }

    public void setUids(Long[] uids) {
        this.uids = uids;
    }

    //    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    public Long getChatUid() {
        return chatUid;
    }

    public void setChatUid(Long chatUid) {
        this.chatUid = chatUid;
    }
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
