package com.quanxiaoha.airobot.aspect;

import java.lang.annotation.*;

/**
 * @imterface: 表示这是一个注解类型,给代码打标记的元数据
 * @Retention: 控制注解的生命周期,RUNTIME:表示程序运行时，JVM 也能识别并读取这个注解
 * @Target: 控制注解能写在什么位置，这里指定为 METHOD，表示这个注解只能加在方法上（通常是 Controller 的接口方法）。
 * @Documented: 标记注解，用于生成 JavaDoc 文档，让文档里也能显示这个注解的说明。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface ApiOperationLog {
    /**
     * API 功能描述
     *
     * @return
     */
    String description() default "";

}