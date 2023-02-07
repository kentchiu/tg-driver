package com.kent.tg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kent.tg.domain.MessagePhoto;
import com.kent.tg.mapper.MessagePhotoMapper;
import com.kent.tg.service.MessagePhotoService;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class MessagePhotoServiceImpl extends ServiceImpl<MessagePhotoMapper, MessagePhoto> implements MessagePhotoService {


    @Override
    public Optional<MessagePhoto> findOneByMessageUid(long messageUid) {
        LambdaQueryWrapper<MessagePhoto> qw = Wrappers.lambdaQuery(MessagePhoto.class);
        qw.eq(MessagePhoto::getMessageUid, messageUid);
        MessagePhoto messagePhoto = getOne(qw);
        return Optional.ofNullable(messagePhoto);
    }
}




