package com.kent.tg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kent.tg.domain.File;
import com.kent.tg.mapper.FileMapper;
import com.kent.tg.service.FileService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {

    @Override
    public Optional<File> findOneByFileUniqueId(String uniqueId) {
        return baseMapper.findOneByFileUniqueId(uniqueId);
    }

}




