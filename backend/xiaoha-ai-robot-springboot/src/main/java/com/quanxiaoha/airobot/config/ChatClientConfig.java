package com.quanxiaoha.airobot.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: 犬小哈
 * @Date: 2025/5/24 17:30
 * @Version: v1.0.0
 * @Description: ChatClient 配置类
 **/
@Configuration
public class ChatClientConfig {
    @Bean
    public ChatClient chatClient(OpenAiChatModel chatModel, ToolCallbackProvider mcpToolCallbackProvider) {
        return ChatClient.builder(chatModel)
                .defaultToolCallbacks(mcpToolCallbackProvider)
                .build();
    }
}