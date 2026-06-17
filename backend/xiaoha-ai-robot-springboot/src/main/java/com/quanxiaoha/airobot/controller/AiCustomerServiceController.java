package com.quanxiaoha.airobot.controller;

import com.google.common.collect.Lists;
import com.quanxiaoha.airobot.advisor.CustomChatMemoryAdvisor;
import com.quanxiaoha.airobot.advisor.CustomStreamLoggerAndMessage2DBAdvisor;
import com.quanxiaoha.airobot.advisor.CustomerServiceAdvisor;
import com.quanxiaoha.airobot.advisor.NetworkSearchAdvisor;
import com.quanxiaoha.airobot.aspect.ApiOperationLog;
import com.quanxiaoha.airobot.domain.mapper.ChatMessageMapper;
import com.quanxiaoha.airobot.model.vo.chat.*;
import com.quanxiaoha.airobot.model.vo.customerService.*;
import com.quanxiaoha.airobot.service.ChatService;
import com.quanxiaoha.airobot.service.CustomerService;
import com.quanxiaoha.airobot.service.SearXNGService;
import com.quanxiaoha.airobot.service.SearchResultContentFetcherService;
import com.quanxiaoha.airobot.utils.PageResponse;
import com.quanxiaoha.airobot.utils.Response;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.List;


/**
 * @Author: 犬小哈
 * @Date: 2025/5/22 12:25
 * @Version: v1.0.0
 * @Description: AI 客服
 **/
@RestController
@RequestMapping("/customer-service")
@Slf4j
public class AiCustomerServiceController {

    @Resource
    private CustomerService customerService;
    @Resource
    private VectorStore vectorStore;

    @Value("${customer-service.model}")
    private String model;
    @Value("${customer-service.temperature}")
    private Double temperature;
    // 新增下面两行
    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    //区别@RequestParam：@RequestParam也能接文件，但@RequestPart更适配multipart/form-data复杂表单（同时传文件 + JSON 参数场景）。
    //文件非必传，如果前端不传file字段，不会直接抛出参数缺失异常，交给 service 自行判断空文件、返回错误提示。
    //MultipartFile:Spring 封装的文件对象，内置常用方法：
    //file.getOriginalFilename()：获取上传文件原始名称（如知识库.md）
    //file.getBytes()：获取文件字节流
    //file.getInputStream()：文件输入流
    //file.getSize()：文件大小
    //file.isEmpty()：判断是否为空文件

    /**
     * 问答 MD 文件上传
     * @param file
     * @return
     */
    @PostMapping(value = "/md/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<?> uploadMarkdownFile(@RequestPart(value = "file", required = true) MultipartFile file) {
        try {
            log.info("开始上传文件，原始文件名：{}", file.getOriginalFilename());
            return customerService.uploadMarkdownFile(file);
        } catch (Exception e) {
            // 打印完整报错堆栈，控制台会显示真实失败原因
            log.error("文件上传接口异常完整堆栈：", e);
            throw e;
        }
    }

    @PostMapping("/md/delete")
    @ApiOperationLog(description = "删除 Markdown 问答文件")
    public Response<?> deleteMarkdownFile(@RequestBody @Validated DeleteMarkdownFileReqVO deleteMarkdownFileReqVO) {
        return customerService.deleteMarkdownFile(deleteMarkdownFileReqVO);
    }

    @PostMapping("/md/list")
    @ApiOperationLog(description = "Markdown 问答文件分页查询")
    public PageResponse<FindMarkdownFilePageListRspVO> findMarkdownFilePageList(@RequestBody @Validated FindMarkdownFilePageListReqVO findMarkdownFilePageListReqVO) {
        return customerService.findMarkdownFilePageList(findMarkdownFilePageListReqVO);
    }


    @PostMapping("/md/update")
    @ApiOperationLog(description = "修改Markdown问答文件信息")
    public Response<?> updateMarkdownFile(@RequestBody @Validated UpdateMarkdownFileReqVO updateMarkdownFileReqVO){
        return customerService.updateMarkdownFile(updateMarkdownFileReqVO);
    }

    /**
     * 流式对话
     * @return
     */
    @PostMapping(value = "/completion", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ApiOperationLog(description = "AI 智能客服对话")
    public Flux<AIResponse> chat(@RequestBody @Validated AiCustomerServiceChatReqVO chatReqVO) {
        String userMessage = chatReqVO.getMessage();

        // 构建 ChatModel
        ChatModel chatModel = OpenAiChatModel.builder()
                .openAiApi(OpenAiApi.builder()
                        .baseUrl(baseUrl)
                        .apiKey(apiKey)
                        .build())
                .build();

        // 动态设置调用的模型名称、温度值
        ChatClient.ChatClientRequestSpec chatClientRequestSpec = ChatClient.create(chatModel)
                .prompt()
                .options(OpenAiChatOptions.builder()
                        .model(model)
                        .temperature(temperature)
                        .build())
                .user(userMessage); // 用户提示词

        // Advisor 集合
        List<Advisor> advisors = Lists.newArrayList();
        advisors.add(new CustomerServiceAdvisor(vectorStore)); // 检索向量库，组合增强提示词

        // 应用 Advisor 集合
        chatClientRequestSpec.advisors(advisors);

        // 流式输出
        return chatClientRequestSpec
                .stream()
                .content()
                .mapNotNull(text -> AIResponse.builder().v(text).build()); // 构建返参 AIResponse
    }
}
