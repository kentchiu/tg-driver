package com.kent.tg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kent.tg.domain.File;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @Entity com.kent.tg.domain.File
 */
@Mapper
@Repository
public interface FileMapper extends BaseMapper<File> {

    @Select("select * from file where file_unique_id = #{uniqueId}")
    Optional<File> findOneByFileUniqueId(@Param("uniqueId") String uniqueId);


}




