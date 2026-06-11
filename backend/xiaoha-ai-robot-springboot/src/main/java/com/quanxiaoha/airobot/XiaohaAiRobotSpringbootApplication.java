package com.quanxiaoha.airobot;

import org.springframework.ai.model.deepseek.autoconfigure.DeepSeekChatAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = DeepSeekChatAutoConfiguration.class)
public class XiaohaAiRobotSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(XiaohaAiRobotSpringbootApplication.class, args);
    }

}
