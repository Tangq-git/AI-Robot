package com.quanxiaoha.airobot.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 自定义业务异常类
 * RuntimeException: 非检查异常,主要是程序运行时的意外/业务逻辑错误(比如空指针,数组越界,参数非法)
 * 业务异常-逻辑上的错误,不是底层必须处理的检查异常
 */
@Getter
@Setter
public class BizException extends RuntimeException{

    //异常码
    private String errorCode;
    //错误信息
    private String errorMessage;

    public BizException(BaseExceptionInterface baseExceptionInterface){
        this.errorCode= baseExceptionInterface.getErrorCode();
        this.errorMessage= baseExceptionInterface.getErrorMessage();
    }
}
