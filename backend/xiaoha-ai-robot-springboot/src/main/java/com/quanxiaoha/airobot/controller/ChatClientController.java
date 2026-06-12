package com.quanxiaoha.airobot.controller;

import com.quanxiaoha.airobot.tool.DateTimeTools;
import com.quanxiaoha.airobot.tool.WeatherTools;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/v2/ai")
public class ChatClientController {

    @Resource
    private ChatClient chatClient;

    @GetMapping("/generate")
    public String generate(@RequestParam(value = "message",defaultValue = "你是谁?")String message){
        return chatClient.prompt()

                .user(message)
                .call()
                .content();
    }

    /**
     * 流式对话
     * @param message
     * @return
     */
    @GetMapping(value = "/generateStream",produces = "text/html;charset=utf-8")
    public Flux<String>generateStream(@RequestParam(value = "message",defaultValue = "今天几号")String message,
                                      @RequestParam(value = "chatId") String chatId){
        //流式输出
        return chatClient.prompt()
                .tools(new DateTimeTools(),new WeatherTools()) //Function Call
                //.system("请你扮演一名超牛逼的健身教练")
                .user(message)//提示词
                .advisors(a->a.param(ChatMemory.CONVERSATION_ID,chatId))
                .stream()//流式输出
                .content();
    }
}
