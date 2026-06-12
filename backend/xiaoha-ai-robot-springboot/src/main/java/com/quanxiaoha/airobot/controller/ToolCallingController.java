package com.quanxiaoha.airobot.controller;


import com.quanxiaoha.airobot.tool.DateTimeTools;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/v13/ai")
public class ToolCallingController {

    @Resource
    private ZhiPuAiChatModel chatModel;
    @GetMapping(value = "/generateStream",produces = "text/html;charset=utf-8")
    public Flux<String> generateStream(@RequestParam(value = "message") String message){
        //1.将datetime注册在工作集中
        ToolCallback[] tools= ToolCallbacks.from(new DateTimeTools());
        // 构建聊天选项配置，设置工具回调功能
        ChatOptions chatOptions = ToolCallingChatOptions.builder()
                .toolCallbacks(tools)
                .build();

        // 构建提示词
        Prompt prompt = new Prompt(new UserMessage(message), chatOptions);

        // 流式输出
        return chatModel.stream(prompt)
                .mapNotNull(chatResponse -> {
                    // 智谱AI 输出消息对象
                    AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
                    // 智谱无独立推理字段，直接取回答文本
                    String text = assistantMessage.getText();
                    return StringUtils.isNotBlank(text) ? text : null;
                });
    }
}
