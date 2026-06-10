package com.quanxiaoha.airobot.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.Objects;

@RestController
@RequestMapping("/v6/ai")
public class AliyunBailianController {

    @Resource
    private OpenAiChatModel chatModel;

    /**
     * 流式对话
     * @param message
     * @return
     */
    @GetMapping(value = "/generateStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<AIResponse> generateStream(@RequestParam(value = "message", defaultValue = "你是谁？") String message) {
        // 系统角色消息
        SystemMessage systemMessage = new SystemMessage("请你扮演一名犬小哈 Java 项目实战专栏的客服人员");
        // 用户角色消息
        UserMessage userMessage = new UserMessage(message);
        // 构建提示词
        Prompt prompt = new Prompt(Arrays.asList(systemMessage, userMessage));

        // 流式输出
        return chatModel.stream(prompt)
                .mapNotNull(chatResponse -> {
                    Generation generation = chatResponse.getResult();
                    String text = generation.getOutput().getText();
                    return AIResponse.builder().v(text).build();
                });

    }
}
