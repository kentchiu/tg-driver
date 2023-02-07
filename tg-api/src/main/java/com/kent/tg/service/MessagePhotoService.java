package com.kent.tg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kent.tg.domain.MessagePhoto;

import java.util.Optional;


public interface MessagePhotoService extends IService<MessagePhoto> {

    Optional<MessagePhoto> findOneByMessageUid(long messageUid);

}
