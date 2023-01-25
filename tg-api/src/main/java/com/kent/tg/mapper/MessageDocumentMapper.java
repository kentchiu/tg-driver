package com.kent.tg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kent.tg.domain.MessageDocument;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Entity com.kent.tg.domain.MessageDocument
 */
@Mapper
@Repository
public interface MessageDocumentMapper extends BaseMapper<MessageDocument> {

}




