package com.quanxiaoha.airobot.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AiCustomerServiceMdStatusEnum {

    PENDING(0,"待处理"),
    VECTORIZING(1,"向量化中"),
    COMPLETED(2,"已完成"),
    FAILED(3,"失败");

    private Integer code;
    private String description;
}
