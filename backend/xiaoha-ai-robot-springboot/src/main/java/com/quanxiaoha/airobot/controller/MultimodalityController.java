package com.quanxiaoha.airobot.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v9/ai")
public class MultimodalityController {

    @Resource
    private OpenAiChatModel chatModel;

    @GetMapping(value = "generateStream",produces = "text/html;charset=utf-8")
    public Flux<String> generateStream(@RequestParam(value = "message") String message){
        //1.创建媒体资源
        Media image=new Media(
                MimeTypeUtils.IMAGE_PNG,
                new ClassPathResource("/image/street-city-night.png")
        );

        //2.附加选项,温度值
        Map<String,Object>metadata=new HashMap<>();
        metadata.put("temperature",0.7);
        //temperature 控制生成文本的随机性：0 表示最确定，1 表示最随机，0.7 是一个平衡创意和稳定性的值。
        //metadata 用来传递模型的配置参数，这里我们设置了 temperature: 0.7。

        // 3. 构建多模态消息
        UserMessage userMessage = UserMessage.builder()
                .text(message)
                .media(image)
                .metadata(metadata)
                .build();
        //4.构建提示词
        Prompt prompt=new Prompt(List.of(userMessage));
        //5.流式调用
        return chatModel.stream(prompt)
                .mapNotNull(chatResponse -> {
                    Generation generation=chatResponse.getResult();
                    return generation.getOutput().getText();
                });
    }
}
