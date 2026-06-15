package com.quanxiaoha.airobot.advisor;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.quanxiaoha.airobot.domain.dos.ChatMessageDO;
import com.quanxiaoha.airobot.domain.mapper.ChatMessageMapper;
import com.quanxiaoha.airobot.model.vo.chat.AiChatReqVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * @Author: 犬小哈
 * @Date: 2025/5/26 16:36
 * @Version: v1.0.0
 * @Description: 自定义对话记忆 Advisor
 * 从数据库读取历史对话,拼接当前请求,实现多轮上下文记忆
 * 执行顺序 order=2（高优先级，请求发给 AI 之前先执行）
 * 根据会话 ID (chatId) 查库，取出该会话最近的历史聊天记录
 * 把历史记录转为 Spring AI 标准消息对象，和当前用户提问合并
 * 生成新请求，带着完整上下文发给大模型，让 AI 记住之前对话
 **/
@Slf4j
@RequiredArgsConstructor
public class CustomChatMemoryAdvisor implements StreamAdvisor{

    private final ChatMessageMapper chatMessageMapper;//操作聊天记录表
    private final AiChatReqVO aiChatReqVO;//前端请求参数,拿到chatId
    private final int limit;//最大加载历史条数

    public int getOrder(){
        return 2;
    }

    public String getName(){
        return this.getClass().getSimpleName();
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        log.info("## 自定义聊天记忆 Advisor...");

        String chatUuid= aiChatReqVO.getChatId();

        // 查询数据库拉取最新的聊天消息
        List<ChatMessageDO> messages = chatMessageMapper.selectList(Wrappers.<ChatMessageDO>lambdaQuery()
                .eq(ChatMessageDO::getChatUuid, chatUuid) // 查询指定对话 UUID 下的聊天记录
                .orderByDesc(ChatMessageDO::getCreateTime) // 查询最新的消息
                .last(String.format("LIMIT %d", limit))); // 仅查询 LIMIT 条

        // 按发布时间升序排列
        List<ChatMessageDO> sortedMessages = messages.stream()
                .sorted(Comparator.comparing(ChatMessageDO::getCreateTime)) // 升序排列
                .toList();
        // 所有消息
        List<Message> messageList = Lists.newArrayList();
        // 将数据库记录转换为对应类型的消息
        for (ChatMessageDO chatMessageDO : sortedMessages) {
            // 消息类型
            String type  = chatMessageDO.getRole();
            if (Objects.equals(type, MessageType.USER.getValue())) { // 用户消息
                Message userMessage = new UserMessage(chatMessageDO.getContent());
                messageList.add(userMessage);
            } else if (Objects.equals(type, MessageType.ASSISTANT.getValue())) { // AI 助手消息
                Message assistantMessage = new AssistantMessage(chatMessageDO.getContent());
                messageList.add(assistantMessage);
            }
        }
        // 除了记忆消息，还需要添加当前用户消息
        messageList.addAll(chatClientRequest.prompt().getInstructions());
        // 构建一个新的 ChatClientRequest 请求对象
        ChatClientRequest processedChatClientRequest = chatClientRequest
                .mutate()
                .prompt(chatClientRequest.prompt().mutate().messages(messageList).build())
                .build();
        return streamAdvisorChain.nextStream(processedChatClientRequest);
    }
}
