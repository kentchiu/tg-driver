package com.kent.tg.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.kent.tg.domain.MessageVideo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @Entity com.kent.tg.domain.MessageVideo
 */
@Mapper
@Repository
public interface MessageVideoMapper extends BaseMapper<MessageVideo> {


    Map<String, Object> findOneByMessageUid(@Param("messageUid") int messageUid);

    IPage<Map<String, Object>> findAll(@Param("page") IPage page, @Param(Constants.WRAPPER) Wrapper<Map<String, Object>> queryWrapper);

}




