package com.kent.tg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kent.tg.domain.BanRule;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Entity com.kent.tg.domain.BanRule
 */
@Mapper
@Repository
public interface BanRuleMapper extends BaseMapper<BanRule> {


}




