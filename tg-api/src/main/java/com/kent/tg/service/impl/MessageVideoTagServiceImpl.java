package com.kent.tg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kent.tg.domain.MessageVideoTag;
import com.kent.tg.mapper.MessageVideoTagMapper;
import com.kent.tg.service.MessageVideoTagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MessageVideoTagServiceImpl extends ServiceImpl<MessageVideoTagMapper, MessageVideoTag>
        implements MessageVideoTagService {

    /**
     * From Tag Table of Database
     */
    private final Map<String, Long> TAG_TABLE = Map.of("WATCHED", 1L, "PINNED", 2L);
    private final Logger logger = LoggerFactory.getLogger(MessageVideoTagServiceImpl.class);


    @Override
    public boolean upsertTagToMessageVideo(String tag, long messageVideoUid) {
        if (TAG_TABLE.containsKey(tag)) {
            LambdaQueryWrapper<MessageVideoTag> queryWrapper = Wrappers.lambdaQuery(MessageVideoTag.class);
            queryWrapper.eq(MessageVideoTag::getMessageVideoUid, messageVideoUid);
            boolean exists = baseMapper.exists(queryWrapper);
            if (!exists) {
                logger.info("Add tag [{}] to messageVideoUid {}", tag, messageVideoUid);
                MessageVideoTag entity = new MessageVideoTag();
                entity.setTagUid(TAG_TABLE.get(tag));
                entity.setMessageVideoUid(messageVideoUid);
                baseMapper.insert(entity);
            } else {
                logger.debug("The messageVideoUid {} already has tag [{}]", messageVideoUid, tag);
            }
            return true;
        } else {
            logger.warn("Unsupported tag: {}", tag);
            return false;
        }
    }
}



