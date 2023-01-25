package com.kent.tg.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Video Tagging DTO")
public class VideoTagDto {


    private String tag;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "VideoTagDto{" +
                "tag='" + tag + '\'' +
                '}';
    }
}
