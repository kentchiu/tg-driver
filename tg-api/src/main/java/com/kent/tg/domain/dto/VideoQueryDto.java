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

}
