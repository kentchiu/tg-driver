package com.kent.tg.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.kent.tg.domain.Chat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Entity com.kent.tg.domain.Chat
 */
@Mapper
@Repository
public interface ChatMapper extends BaseMapper<Chat> {
    List<Map<String, Object>> findAll(@Param(Constants.WRAPPER) Wrapper<Map<String, Object>> queryWrapper);

    @Select("SELECT chat.uid  uid, COUNT(1) message_count FROM chat LEFT JOIN message ON message.chat_id = chat.chat_id ${ew.customSqlSegment} GROUP BY chat.chat_id")
    List<Map<String, Object>> countMessage(@Param(Constants.WRAPPER) Wrapper<Map<String, Object>> queryWrapper);

    /**
     * @param queryWrapper
     * @return
     */
    @Deprecated
    List<Map<String, Object>> findAllWithMessageCount(@Param(Constants.WRAPPER) Wrapper<Map<String, Object>> queryWrapper);
}




