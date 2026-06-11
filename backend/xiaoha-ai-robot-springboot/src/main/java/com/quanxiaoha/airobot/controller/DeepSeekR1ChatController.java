package com.quanxiaoha.airobot.controller;

import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekAssistantMessage;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping("/v1/ai")
@ConditionalOnProperty(prefix = "deepseek.controller", name = "enable", havingValue = "true", matchIfMissing = false)
public class DeepSeekR1ChatController {

    @Resource
    private DeepSeekChatModel chatModel;

    /**
     * 流式对话
     * @param message
     * @return
     */
    @GetMapping(value = "/generateStream",produces = "text/html;charset=UTF-8")
    public Flux<String> generateStream(@RequestParam(value = "message",defaultValue = "你是谁?")String message){
        //构建提示词
        Prompt prompt=new Prompt(new UserMessage(message));

        //使用原子布尔值跟踪分割线状态(每个请求独立)
        AtomicBoolean needSeparator=new AtomicBoolean(true);

        //流式输出
        return chatModel.stream(prompt)
                .mapNotNull(chatResponse -> {
                    //获取响应内容
                    DeepSeekAssistantMessage deepSeekAssistantMessage=(DeepSeekAssistantMessage) chatResponse.getResult().getOutput();
                    //推理内容
                    String reasoningContent=deepSeekAssistantMessage.getReasoningContent();
                    //推理结束后的正式回答
                    String text=deepSeekAssistantMessage.getText();

                    //是否是正是回答
                    boolean isTextResponse=false;
                    //若推理内容有值,则响应推理内容,否则,说明推理结束,响应正是回答
                    String rawContent;
                    if(Objects.isNull(text)){
                        rawContent=reasoningContent;
                    }else{
                        rawContent=text;
                        isTextResponse=true;//标记为正式回答
                    }

                    //处理换行
                    String processed=StringUtils.isNotBlank(rawContent)?rawContent.replace("\n","<br>"):rawContent;

                    //正式回答之前,添加一个分割线
                    if(isTextResponse
                    &&needSeparator.compareAndSet(true,false)){
                        processed="<hr>"+processed;
                    }
                    return processed;
                });
    }
}
