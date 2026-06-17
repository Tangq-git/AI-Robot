package com.quanxiaoha.airobot.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class DateTimeTools {

    /**
     * @Tool 注解必须写清晰描述，大模型靠这个判断何时调用该方法
     * @return 当前系统日期时间
     */
    @Tool(description = "获取当前系统的标准日期和时间，当用户询问今天几号、现在几点、当前日期时调用此工具")
    public String getCurrentDateTime() {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info("执行日期工具，获取当前时间：{}", now);
        return now;
    }
}