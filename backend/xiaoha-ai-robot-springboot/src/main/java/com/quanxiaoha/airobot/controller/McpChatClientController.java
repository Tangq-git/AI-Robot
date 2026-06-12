package com.quanxiaoha.airobot.controller;

import com.quanxiaoha.airobot.model.AIResponse;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.Generation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/mcp/ai")
public class McpChatClientController {

    @Resource
    private ChatClient chatClient;

    /**
     * 流式对话
     * @param message
     * @return
     * */
    @GetMapping(value = "/generateStream",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<AIResponse> generateStream(@RequestParam(value = "message") String message,
                                           @RequestParam(value = "message") String chatId){

        return chatClient.prompt()
                .user(message)
                //.advisors(a -> a.param(ChatMemory.CONVERSATION_ID,chatId))
                .stream()
                .chatResponse()
                .mapNotNull(chatResponse -> {
                    Generation generation=chatResponse.getResult();
                    String text = generation.getOutput().getText();
                    return AIResponse.builder().v(text).build();
                });
    }
}
