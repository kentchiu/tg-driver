package com.kent.tg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kent.tg.domain.File;

import java.util.Optional;

public interface FileService extends IService<File> {

    Optional<File> findOneByFileUniqueId(String uniqueId);

}
