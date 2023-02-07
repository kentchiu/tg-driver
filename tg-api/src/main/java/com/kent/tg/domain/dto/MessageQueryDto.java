package com.kent.tg.domain.dto;

import com.kent.base.domain.BaseQuery;
import com.kent.base.domain.Options;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Message Query DTO")
public class MessageQueryDto extends BaseQuery {

    private Long chatUid;
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
}
