package com.quanxiaoha.airobot.utils;

import com.quanxiaoha.airobot.exception.BaseExceptionInterface;
import com.quanxiaoha.airobot.exception.BizException;
import lombok.Data;

import java.io.Serializable;

@Data
//Java 序列化接口
/*
序列化：把 Java 对象 → 二进制 / 字符串，方便传输、存储。
反序列化：反过来，二进制 / 字符串 → 还原成 Java 对象。
implements Serializable 就是告诉 JVM：这个类允许被序列化。
 */
public class Response<T> implements Serializable {
    // 是否成功，默认为 true
    private boolean success = true;
    // 响应消息
    private String message;
    // 异常码
    private String errorCode;
    // 响应数据
    private T data;

    /**
     * 只告知 “操作成功”
     * @return
     * @param <T>
     */
    public static<T> Response<T>success(){
        Response<T>response=new Response<>();
        return response;
    }

    /**
     * 查询接口,把查询结果塞入data返回
     * @param data
     * @return
     * @param <T>
     */
    public static<T> Response<T> success(T data){
        Response<T> response=new Response<>();
        response.setData(data);
        return response;
    }

    /**
     * 纯失败,无提示,无错误码,很少用
     * @return
     * @param <T>
     */
    public static <T> Response<T> fail(){
        Response<T> response=new Response<>();
        response.setSuccess(false);
        return response;
    }

    /**
     * 只传错误提示文案
     * @param errorMessage
     * @return
     * @param <T>
     */
    public static<T> Response<T> fail(String errorMessage){
        Response<T> response=new Response<>();
        response.setSuccess(false);
        response.setMessage(errorMessage);
        return response;
    }

    /**
     * 错误码,错误提示都返回
     * @param errorCode
     * @param errorMessage
     * @return
     * @param <T>
     */
    public static<T> Response<T> fail(String errorCode,String errorMessage){
        Response<T> response=new Response<>();
        response.setSuccess(false);
        response.setMessage(errorMessage);
        response.setErrorCode(errorCode);
        return response;
    }

    /**
     * 专门接收你自定义的业务异常对象，自动从异常里取出 errorCode（错误码）和 errorMessage（错误提示），组装成统一响应结果。
     * @param bizException
     * @return
     * @param <T>
     */
    public static<T> Response<T> fail(BizException bizException){
        Response<T> response = new Response<>();
        response.setSuccess(false);
        response.setErrorCode(bizException.getErrorCode());
        response.setMessage(bizException.getErrorMessage());
        return response;
    }

    /**
     * 直接用错误枚举返回失败 → 用 fail(BaseExceptionInterface)
     * @param baseExceptionInterface
     * @return
     * @param <T>
     */
    public static <T> Response<T> fail(BaseExceptionInterface baseExceptionInterface){
        Response<T> response = new Response<>();
        response.setSuccess(false);
        response.setErrorCode(baseExceptionInterface.getErrorCode());
        response.setMessage(baseExceptionInterface.getErrorMessage());
        return response;
    }
}
