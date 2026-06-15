package com.quanxiaoha.airobot.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.airobot.domain.dos.ChatDO;
import com.quanxiaoha.airobot.domain.dos.ChatMessageDO;
import com.quanxiaoha.airobot.domain.mapper.ChatMapper;
import com.quanxiaoha.airobot.domain.mapper.ChatMessageMapper;
import com.quanxiaoha.airobot.enums.ResponseCodeEnum;
import com.quanxiaoha.airobot.exception.BizException;
import com.quanxiaoha.airobot.model.vo.chat.*;
import com.quanxiaoha.airobot.service.ChatService;
import com.quanxiaoha.airobot.utils.PageResponse;
import com.quanxiaoha.airobot.utils.Response;
import com.quanxiaoha.airobot.utils.StringUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 对话
 */
@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    @Resource
    private ChatMapper chatMapper;
    @Resource
    private ChatMessageMapper chatMessageMapper;
    /**
     * 新建对话
     * 在业务层中，通过 UUID.randomUUID().toString() 来生成此对话的 UUID ;
     * 调用 StringUtil.truncate() 方法，截取用户发送的消息前 20 位，作为对话摘要；
     * 构建 ChatDO 数据库实体类，将对话记录写入到数据库中；
     * 将摘要、UUID 返回给前端；
     * @param newChatReqVO
     * @return
     */
    public Response<NewChatRspVO>newChat(NewChatReqVO newChatReqVO){
        String message=newChatReqVO.getMessage();

        String uuid= UUID.randomUUID().toString();
        String summary= StringUtil.truncate(message,20);

        //存储对话记录到数据库中
        chatMapper.insert(ChatDO.builder()
                .summary(summary)
                .uuid(uuid)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build());
        //奖摘要,UUID返回给前端
        return Response.success(NewChatRspVO.builder()
                .uuid(uuid)
                .summary(summary)
                .build());
    }

    @Override
    public PageResponse<FindChatHistoryMessagePageListRspVO> findChatHistoryMessagePageList(FindChatHistoryMessagePageListReqVO findChatHistoryMessagePageListReqVO) {
        // 获取当前页、以及每页需要展示的数据数量
        Long current = findChatHistoryMessagePageListReqVO.getCurrent();
        Long size = findChatHistoryMessagePageListReqVO.getSize();
        String chatId = findChatHistoryMessagePageListReqVO.getChatId();

        // 执行分页查询
        Page<ChatMessageDO> chatMessageDOPage = chatMessageMapper.selectPageList(current, size, chatId);
        List<ChatMessageDO> chatMessageDOS = chatMessageDOPage.getRecords();
        // DO 转 VO
        List<FindChatHistoryMessagePageListRspVO> vos = null;
        if (CollUtil.isNotEmpty(chatMessageDOS)) {
            vos = chatMessageDOS.stream()
                    .map(chatMessageDO -> FindChatHistoryMessagePageListRspVO.builder() // 构建返参 VO 实体类
                            .id(chatMessageDO.getId())
                            .chatId(chatMessageDO.getChatUuid())
                            .content(chatMessageDO.getContent())
                            .role(chatMessageDO.getRole())
                            .createTime(chatMessageDO.getCreateTime())
                            .build())
                    // 升序排序
                    .sorted(Comparator.comparing(FindChatHistoryMessagePageListRspVO::getCreateTime))
                    .collect(Collectors.toList());
        }
        return PageResponse.success(chatMessageDOPage, vos);
    }

    /**
     * 查询历史对话
     *
     * @param findChatHistoryPageListReqVO
     * @return
     */
    @Override
    public PageResponse<FindChatHistoryPageListRspVO> findChatHistoryPageList(FindChatHistoryPageListReqVO findChatHistoryPageListReqVO) {
        // 获取当前页、以及每页需要展示的数据数量
        Long current = findChatHistoryPageListReqVO.getCurrent();
        Long size = findChatHistoryPageListReqVO.getSize();

        // 执行分页查询
        Page<ChatDO> chatDOPage = chatMapper.selectPageList(current, size);

        // 获取查询结果
        List<ChatDO> chatDOS = chatDOPage.getRecords();

        // DO 转 VO
        List<FindChatHistoryPageListRspVO> vos = null;
        if (CollUtil.isNotEmpty(chatDOS)) {
            vos = chatDOS.stream()
                    .map(chatDO -> FindChatHistoryPageListRspVO.builder() // 构建返参 VO
                            .id(chatDO.getId())
                            .uuid(chatDO.getUuid())
                            .summary(chatDO.getSummary())
                            .updateTime(chatDO.getUpdateTime())
                            .build())
                    .collect(Collectors.toList());
        }

        return PageResponse.success(chatDOPage, vos);
    }

    /**
     * 重命名
     * @param renameChatReqVO
     * @return
     */
    @Override
    public Response<?> renameChatSummary(RenameChatReqVO renameChatReqVO) {
        // 1. 从请求VO中取出参数
        Long chatId = renameChatReqVO.getId();
        String summary = renameChatReqVO.getSummary();

        // 2. 构建DO对象，调用 MP 自带的 updateById 按主键更新
        chatMapper.updateById(ChatDO.builder()
                .id(chatId)
                .summary(summary)
                .build());

        // 3. 返回统一成功响应
        return Response.success();
    }

    /**
     * 删除对话
     *
     * @param deleteChatReqVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<?> deleteChat(DeleteChatReqVO deleteChatReqVO) {
        // 对话 UUID
        String uuid = deleteChatReqVO.getUuid();

        // 删除对话
        int count = chatMapper.delete(Wrappers.<ChatDO>lambdaQuery()
                .eq(ChatDO::getUuid, uuid));

        // 如果删除操作影响的行数为 0，说明想要删除的对话不存在
        if (count == 0) {
            throw new BizException(ResponseCodeEnum.CHAT_NOT_EXISTED);
        }

        // 批量删除对话下的所有消息
        chatMessageMapper.delete(Wrappers.<ChatMessageDO>lambdaQuery()
                .eq(ChatMessageDO::getChatUuid, uuid));

        return Response.success();
    }

}
