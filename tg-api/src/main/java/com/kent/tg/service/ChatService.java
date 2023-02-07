package com.kent.tg.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kent.tg.domain.Chat;

import java.util.List;
import java.util.Map;


public interface ChatService extends IService<Chat> {

    List<Map<String, Object>> findAllWithMessageCount(Wrapper<Map<String, Object>> queryWrapper);

}
