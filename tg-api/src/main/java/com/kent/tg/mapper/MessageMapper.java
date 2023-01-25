package com.kent.tg.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.kent.tg.domain.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @Entity com.kent.tg.domain.Message
 */
@Mapper
@Repository
public interface MessageMapper extends BaseMapper<Message> {

    IPage<Map<String, Object>> findAll(@Param("page") IPage page, @Param(Constants.WRAPPER) Wrapper<Map<String, Object>> queryWrapper);

    int countFindAll(@Param(Constants.WRAPPER) Wrapper<Map<String, Object>> queryWrapper);

}




