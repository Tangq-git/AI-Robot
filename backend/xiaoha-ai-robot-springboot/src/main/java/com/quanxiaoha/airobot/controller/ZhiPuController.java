package com.quanxiaoha.airobot.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/v4/ai")
public class ZhiPuController {

    @Resource
    private ZhiPuAiChatModel chatModel;
    /**
     * 流式对话
     * @param message
     * @return
     */
    @GetMapping(value = "/generateStream",produces = "text/html;charset=utf-8")
    public Flux<String> generateStream(String message){
        //构建提示词
        Prompt prompt=new Prompt(new UserMessage(message));
        //流式输出
        return chatModel.stream(prompt)
                .mapNotNull(chatResponse -> chatResponse.getResult().getOutput().getText());
    }
}
