package com.quanxiaoha.airobot.enums;

import com.quanxiaoha.airobot.exception.BaseExceptionInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCodeEnum implements BaseExceptionInterface {

    // ----------- 通用异常状态码 -----------
    SYSTEM_ERROR("10000", "出错啦，后台小哥正在努力修复中..."),
    PARAM_NOT_VALID("10001", "参数错误"),
    // ----------- 业务异常状态码 -----------
    CHAT_NOT_EXISTED("20000", "此对话不存在"),
    UPLOAD_FILE_CANT_EMPTY("20001", "上传文件不能为空"),
    ONLY_SUPPORT_MARKDOWN("20002", "仅支持 Markdown 文件（.md 后缀）"),
    UPLOAD_FILE_FAILED("20003", "文件上传失败"),
    ;
    private String errorCode;
    private String errorMessage;
}
