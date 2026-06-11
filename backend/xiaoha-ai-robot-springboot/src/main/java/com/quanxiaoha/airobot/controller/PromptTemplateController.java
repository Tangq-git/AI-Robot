package com.quanxiaoha.airobot.controller;

import com.quanxiaoha.airobot.model.AIResponse;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.awt.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v7/ai")
public class PromptTemplateController {


    @Resource
    private OpenAiChatModel chatModel;

    @Value("classpath:/prompts/code-assistant.st")
    private org.springframework.core.io.Resource templateResource;

    /**
     * 智能代码生成
     * @param message
     * @param lang
     * @return
     */
    @GetMapping(value = "/generateStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<AIResponse> generateStream(@RequestParam(value = "message") String message,
                                           @RequestParam(value = "lang") String lang) {

        // 提示词模板
        PromptTemplate promptTemplate = new PromptTemplate(templateResource);
        // 填充提示词占位符，转换为 Prompt 提示词对象
        Prompt prompt = promptTemplate.create(Map.of("description", message, "lang", lang));

        // 流式输出
        return chatModel.stream(prompt)
                .mapNotNull(chatResponse -> {
                    Generation generation = chatResponse.getResult();
                    String text = generation.getOutput().getText();
                    return AIResponse.builder().v(text).build();
                });
    }

    /**
     * 智能代码生成 2
     * @param message
     * @param lang
     * @return
     */
    @GetMapping(value = "/generateStream2", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    //value:前端传给后端的参数名字,String:我自己随便起的变量名
    //@RequestParam:拿网址？后面的参数
    //@RequestParam(value = "message") String message:我要从网址里，拿一个叫 message 的参数，存到我的变量里。
    public Flux<AIResponse> generateStream2(@RequestParam(value = "message") String message,
                                            @RequestParam(value = "lang") String lang) {
        // 提示词模板
        PromptTemplate promptTemplate = PromptTemplate.builder()
                .renderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build()) // 自定义占位符
                .template("""
                        你是一位资深 <lang> 开发工程师。请严格遵循以下要求编写代码：
                        1. 功能描述：<description>
                        2. 代码需包含详细注释
                        3. 使用业界最佳实践
                        """)
                .build();

        // 填充提示词占位符，转换为 Prompt 提示词对象
        Prompt prompt = promptTemplate.create(Map.of("description", message, "lang", lang));

        // 流式输出
        return chatModel.stream(prompt)
                .mapNotNull(chatResponse -> {
                    Generation generation = chatResponse.getResult();
                    String text = generation.getOutput().getText();
                    return AIResponse.builder().v(text).build();
                });
    }

    /**
     * 智能代码生成 3
     * @param message
     * @param lang
     * @return
     */
    @GetMapping(value = "/generateStream3", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<AIResponse> generateStream3(@RequestParam(value = "message") String message,
                                            @RequestParam(value = "lang") String lang) {

        //1.定义系统角色提示词(给AI定人设)
        String systemPrompt= """
                    你是一位资深{lang}开发工程师,已经从业数十年,经验非常丰富.
                    """;
        //2.创建系统角色模板
        //SystemPromptTemplate:专门用来创建系统角色消息的模板工具类
        SystemPromptTemplate systemPromptTemplate=new SystemPromptTemplate(systemPrompt);

        //3.填充占位符{lang},生成最终的系统消息
        //Map.of("lang",lang):把{lang}换成变量 lang 的值
        //Spring AI规定,要替换占位符,必须传入一个Map,key是占位符的名字,value是要替换的值
        Message systemMessage=systemPromptTemplate.createMessage(Map.of("lang",lang));

        //用户角色提示词模板
        //用户发给AI的请求:比如写一个登录接口,写一个复制按钮
        String userPrompt= """
                请严格遵循以下要求编写代码:
                1.功能描述:{description}
                2.代码需包含详细注释
                3.使用业界最佳实践
                """;

        //创建模板
        //用户消息模板
        PromptTemplate promptTemplate=new PromptTemplate(userPrompt);
        //填充占位符,生成用户信息
        //description:占位符 message:前端传过来的用户请求
        Message userMessage=promptTemplate.createMessage(Map.of("description",message));
        //把用户端需求,包装成一条用户消息发给AI

        //把系统/用户消息打包成一个包裹,准备发给AI
        //List:两条消息放进一个列表
        Prompt prompt=new Prompt(List.of(systemMessage,userMessage));

        //chatModel:我配置的大模型
        //.stream:流式发送,写一个字返回一个字
        //mapNotNull()：只传有效内容，空的自动过滤
        return chatModel.stream(prompt) //打包好的提示词发给AI,开始流式接收
                .mapNotNull(chatResponse -> { //AI每返回一小段数据我就拿过来处理一下,空的直接丢掉,chatResponse是AI返回的原始相应包
                    Generation generation=chatResponse.getResult();
                    /*
                    * {
                          "output": {
                            "text": "public class Hello..."
                          }
                        }
                    * */
                    String text=generation.getOutput().getText();//提取纯文字内容
                    return AIResponse.builder().v(text).build();
                    //builder:建造者模式
                    //前端代码会写:event.data.v,后端返回{"v":"xxx"},前端就拿v的内容
                });
        }
    }
