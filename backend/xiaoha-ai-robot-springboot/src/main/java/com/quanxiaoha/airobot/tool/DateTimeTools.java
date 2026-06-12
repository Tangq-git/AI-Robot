package com.quanxiaoha.airobot.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;

import java.time.LocalDateTime;

@Slf4j
public class DateTimeTools {

    @Tool(description = "获取当前日期和时间")
    //Tool:把这个 Java 方法暴露成 AI 可调用的工具函数。
    //description：给大模型看的功能描述，AI 会根据描述判断：用户提问需不需要调用这个方法。
    String getCurrentDateTime(){
        return LocalDateTime.now().toString();
        //.toString()：把时间对象转成标准字符串（例：2026-06-11T15:30:20），方便 AI 解析、展示。
    }
}
