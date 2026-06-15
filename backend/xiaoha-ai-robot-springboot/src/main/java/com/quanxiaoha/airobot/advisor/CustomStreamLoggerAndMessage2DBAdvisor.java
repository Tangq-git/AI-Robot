package com.quanxiaoha.airobot.advisor;
import com.quanxiaoha.airobot.domain.dos.ChatMessageDO;
import com.quanxiaoha.airobot.domain.mapper.ChatMessageMapper;
import com.quanxiaoha.airobot.model.vo.chat.AiChatReqVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;
@Slf4j
@RequiredArgsConstructor
//实现 Spring AI 流式专属接口 StreamAdvisor，代表这是流式链路拦截器，仅作用于流式对话
public class CustomStreamLoggerAndMessage2DBAdvisor implements StreamAdvisor{

    //final：保证这些变量在初始化后不能被修改，线程安全、代码更健壮。
    //1.用来调用 insert() 方法，把用户提问、AI 回答写入数据库表（比如 t_chat_message）。
    private final ChatMessageMapper chatMessageMapper;
    //2.保存当前对话的上下文，比如 ChatId、用户提问内容，写入数据库时需要用到这些字段。
    private final AiChatReqVO aiChatReqVO;
    //3.用来包裹数据库写入逻辑，保证「用户消息和 AI 回答要么都写入成功，要么都不写」，避免数据不一致。
    private final TransactionTemplate transactionTemplate;

    @Override
    public int getOrder() {
        return 99; // order 值越小，越先执行
    }
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    /**
     * 前端发起流式 AI 对话请求 → 进入 Spring AI 责任链
     * 执行 CustomStreamLoggerAdvisor → 调用 nextStream() 放行请求到 AI 大模型
     * AI 分段返回数据，每一段触发 doOnNext：打印分片 + 拼接内容
     * 场景 1（正常）：AI 输出完毕 → 触发 doOnComplete，打印完整回答
     * 场景 2（异常）：中途报错 → 触发 doOnError，打印已接收内容 + 异常
     * 处理后的流原路返回给前端，实现日志 + 流式输出两不误
     */
    //ChatClientRequest chatClientRequest：AI 客户端请求对象，包含用户提问、模型参数、提示词等
    //StreamAdvisorChain streamAdvisorChain：Advisor 责任链，调用 nextStream() 放行请求，执行后续链路（调用大模型、返回流式结果）
    //返回值 Flux<ChatClientResponse>：Reactor 流式对象，对应 AI 分段返回的数据（流式输出）
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain){
        // 对话 UUID
        String chatUuid = aiChatReqVO.getChatId();
        // 用户消息
        String userMessage = aiChatReqVO.getMessage();
        //责任链放行，把请求交给下一个 Advisor / 直接调用 AI 模型
        Flux<ChatClientResponse> chatClientResponseFlux = streamAdvisorChain.nextStream(chatClientRequest);
        //作用：拼接 AI 分段返回的内容，最终得到完整回答
        AtomicReference<StringBuilder> fullContent = new AtomicReference<>(new StringBuilder());

        // 返回处理后的流
        return chatClientResponseFlux
                .doOnNext(response -> {//Flux 每推送一个数据分片就触发一次（对应前端看到的 “逐字输出”）
                    // 逐块收集内容:AI 单次返回的一小段文本
                    String chunk = response.chatResponse().getResult().getOutput().getText();

                    log.info("## chunk: {}", chunk);

                    // 若 chunk 块不为空，则追加到 fullContent 中
                    //从 AtomicReference 中取出 StringBuilder 做拼接。
                    if (chunk != null) {
                        fullContent.get().append(chunk);
                    }
                })
                .doOnComplete(() -> {//当 AI 把所有内容推送完毕、流式输出正常结束时触发
                    // 流完成后打印完整回答
                    String completeResponse = fullContent.get().toString();
                    log.info("\n==== FULL AI RESPONSE ====\n{}\n========================", completeResponse);

                    //开启编程式事务
                    transactionTemplate.execute(status -> {
                        try{
                            // 1. 存储用户消息
                            chatMessageMapper.insert(ChatMessageDO.builder()
                                    .chatUuid(chatUuid)
                                    .content(userMessage)
                                    .role(MessageType.USER.getValue()) // 用户消息
                                    .createTime(LocalDateTime.now())
                                    .build());

                            // 2. 存储 AI 回答
                            chatMessageMapper.insert(ChatMessageDO.builder()
                                    .chatUuid(chatUuid)
                                    .content(completeResponse)
                                    .role(MessageType.ASSISTANT.getValue()) // AI 回答
                                    .createTime(LocalDateTime.now())
                                    .build());

                            return true;
                        }catch(Exception ex){
                            status.setRollbackOnly();
                            log.error("",ex);
                        }
                        return false;
                    });
                })
                .doOnError(error -> {
                    // 出错时打印已收集的部分
                    //流式过程中发生异常（断网、AI 接口报错、程序异常）触发
                    //打印已经接收到的部分回答 + 异常堆栈，方便线上排查问题。
                    String partialResponse = fullContent.get().toString();
                    log.error("## Stream 流出现错误，已收集回答如下: {}", partialResponse, error);
                });
    }


}
