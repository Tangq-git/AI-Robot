package com.quanxiaoha.airobot.exception;

/**
 * 响应工具类:统一封装接口返回数据格式的工具类,核心作用是让所有接口的返回结果都长成同一个标准结果,方便前端统一处理
 * BaseExceptionInterface: 定义异常的标准规范(比如错误码,错误信息)
 * BizException:业务异常类,抛异常时会带上自定义错误码和信息
 */

/**
 * 通用异常接口
 */
public interface BaseExceptionInterface {
    String getErrorCode();

    String getErrorMessage();
}
