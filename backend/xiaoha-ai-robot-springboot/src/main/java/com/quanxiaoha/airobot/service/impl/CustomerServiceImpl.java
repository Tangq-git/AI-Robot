package com.quanxiaoha.airobot.service.impl;

import com.quanxiaoha.airobot.domain.dos.AiCustomerServiceMdStorageDO;
import com.quanxiaoha.airobot.domain.mapper.AiCustomerServiceMdStorageMapper;
import com.quanxiaoha.airobot.enums.AiCustomerServiceMdStatusEnum;
import com.quanxiaoha.airobot.enums.ResponseCodeEnum;
import com.quanxiaoha.airobot.exception.BizException;
import com.quanxiaoha.airobot.service.CustomerService;
import com.quanxiaoha.airobot.utils.Response;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @Author: 犬小哈
 * @Date: 2025/8/11 15:48
 * @Version: v1.0.0
 * @Description: AI 客服
 **/
@Service
@Slf4j
public class CustomerServiceImpl {

    @Value("${customer-service.md-storage-path}")
    private String mdStoragePath;

    @Resource
    private AiCustomerServiceMdStorageMapper aiCustomerServiceMdStorageMapper;

}
