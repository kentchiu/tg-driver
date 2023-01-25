package com.kent.tg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kent.tg.domain.MessageText;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Entity com.kent.tg.domain.MessageText
 */
@Mapper
@Repository
public interface MessageTextMapper extends BaseMapper<MessageText> {

}




