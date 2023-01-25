package com.kent.tg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kent.tg.domain.MessageVideoTag;

public interface MessageVideoTagService extends IService<MessageVideoTag> {

    boolean upsertTagToMessageVideo(String tag, long messageVideoUid);
}
