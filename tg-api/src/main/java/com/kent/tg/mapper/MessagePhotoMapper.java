package com.kent.tg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kent.tg.domain.MessagePhoto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Entity com.kent.tg.domain.MessagePhoto
 */
@Mapper
@Repository
public interface MessagePhotoMapper extends BaseMapper<MessagePhoto> {

}




