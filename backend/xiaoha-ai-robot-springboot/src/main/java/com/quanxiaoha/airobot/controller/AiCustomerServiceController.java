package com.quanxiaoha.airobot.controller;

import com.google.common.collect.Lists;
import com.quanxiaoha.airobot.advisor.CustomChatMemoryAdvisor;
import com.quanxiaoha.airobot.advisor.CustomStreamLoggerAndMessage2DBAdvisor;
import com.quanxiaoha.airobot.advisor.NetworkSearchAdvisor;
import com.quanxiaoha.airobot.aspect.ApiOperationLog;
import com.quanxiaoha.airobot.domain.mapper.ChatMessageMapper;
import com.quanxiaoha.airobot.model.vo.chat.*;
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
    public Response<?> uploadMarkdownFile(@RequestPart(value = "file", required = false) MultipartFile file) {
        return customerService.uploadMarkdownFile(file);
    }
}
