package com.quanxiaoha.airobot.config;

import com.quanxiaoha.airobot.advisor.MyLoggerAdvisor;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
// 替换为阿里云兼容的OpenAI模型
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Resource
    private ChatMemory chatMemory;

    /**
     * 初始化 ChatClient 客户端（现在绑定阿里云通义）
     * @param chatModel 改为 OpenAiChatModel
     * @return
     */
    @Bean
    public ChatClient chatClient(ZhiPuAiChatModel chatModel) {
        return ChatClient.builder(chatModel)
                //.defaultSystem("请你扮演一名唐茜Java 项目实战专栏的客服人员")
                .defaultAdvisors(new SimpleLoggerAdvisor(),
//                        new MyLoggerAdvisor())
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }
}