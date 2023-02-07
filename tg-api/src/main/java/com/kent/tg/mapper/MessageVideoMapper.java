package com.kent.tg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kent.tg.domain.MessageVideo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Entity com.kent.tg.domain.MessageVideo
 */
@Mapper
@Repository
public interface MessageVideoMapper extends BaseMapper<MessageVideo> {

//
//    Map<String, Object> findOneByMessageUid(@Param("messageUid") long messageUid);
//
//    /**
//     * video exist in message table but not in message_video table
//     *
//     * @return
//     */
//    List<MessageVideo> findMissingVideos();
//
//    /**
//     * video has thumbnail but not the file unique id of thumbnail not exist in file table
//     *
//     * @return
//     */
//    List<MessageVideo> findMissingThumbnails();

}




