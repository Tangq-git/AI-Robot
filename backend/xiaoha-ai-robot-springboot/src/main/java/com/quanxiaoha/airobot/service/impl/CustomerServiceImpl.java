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
public class CustomerServiceImpl implements CustomerService{

    @Value("${customer-service.md-storage-path}")
    private String mdStoragePath;

    @Resource
    private AiCustomerServiceMdStorageMapper aiCustomerServiceMdStorageMapper;

    public Response<?> uploadMarkdownFile(MultipartFile file){
        if(file==null||file.isEmpty()){
            throw new BizException(ResponseCodeEnum.UPLOAD_FILE_CANT_EMPTY);
        }

        String originalFilename=StringUtils.trimToEmpty(file.getOriginalFilename());

        if(StringUtils.isBlank(originalFilename)|| ! isMarkdownFile(originalFilename)){
            throw new BizException(ResponseCodeEnum.ONLY_SUPPORT_MARKDOWN);
        }

        try{
            //重新生成文件名(防止文件名冲突导致覆盖)
            String newFilename=UUID.randomUUID().toString()+"-"+originalFilename;
            //构建存储路径
            Path storageDirectory=Paths.get(mdStoragePath);
            Path targetPath=storageDirectory.resolve(newFilename);

            //确保目录存在
            Files.createDirectories(storageDirectory);

            //保存文件
//            file.transferTo(targetPath.toFile());
            // 【修改点】改用NIO复制流，替代transferTo，解决Windows拒绝访问
            Files.copy(file.getInputStream(), targetPath);
            //记录操作日志
            log.info("## Markdown 问答文件存储成功,文件名:{}->存储路径:{}",originalFilename,targetPath);

            //存储入库
            aiCustomerServiceMdStorageMapper.insert(AiCustomerServiceMdStorageDO.builder()
                            .originalFileName(originalFilename)
                            .newFileName(newFilename)
                            .filePath(targetPath.toString())
                            .fileSize(file.getSize())
                            .status(AiCustomerServiceMdStatusEnum.PENDING.getCode())
                            .createTime(LocalDateTime.now())
                            .updateTime(LocalDateTime.now())
                    .build());
            return Response.success();
        }catch(IOException e){
            log.error("## Markdown 问答文件上传失败:{}",originalFilename,e);
            throw new BizException(ResponseCodeEnum.UPLOAD_FILE_FAILED);
        }

    }

    /**
     * 验证文件是否为Markdown格式
     */
    private boolean isMarkdownFile(String filename){
        if(StringUtils.isBlank(filename)){
            return false;
        }

        String extension=FilenameUtils.getExtension(filename);
        return StringUtils.equalsIgnoreCase(extension,"md");
    }

}
