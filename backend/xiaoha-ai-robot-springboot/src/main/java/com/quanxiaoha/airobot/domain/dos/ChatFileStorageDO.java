package com.quanxiaoha.airobot.domain.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_ai_customer_service_md_storage") // 绑定原有表名不变
public class ChatFileStorageDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 原始文件名 */
    private String originalFileName;
    /** 重命名后的文件名 */
    private String newFileName;
    /** 文件本地存储路径 */
    private String filePath;
    /** 文件大小，字节 */
    private Long fileSize;
    /** 文件类型 md / pdf / txt */
    private String fileType;
    /** 状态 0待处理 1向量化中 2已完成 3失败 */
    private Integer status;
    /** 备注/异常信息 */
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}