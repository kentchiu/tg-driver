package com.kent.tg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kent.tg.domain.Tag;
import com.kent.tg.mapper.TagMapper;
import com.kent.tg.service.TagService;
import org.springframework.stereotype.Service;

@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

}




