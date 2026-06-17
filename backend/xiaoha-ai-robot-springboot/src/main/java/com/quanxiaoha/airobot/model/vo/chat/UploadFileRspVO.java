package com.quanxiaoha.airobot.model.vo.chat;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UploadFileRspVO {
    // 文件库主键ID
    private Long fileId;
    // 原始文件名
    private String originalFileName;
    // 文件类型
    private String fileType;
    // 完整解析文本
    private String fullText;
    // 当前处理状态
    private Integer status;
}