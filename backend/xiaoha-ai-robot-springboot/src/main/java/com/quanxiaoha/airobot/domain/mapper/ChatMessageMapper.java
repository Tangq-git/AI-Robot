package com.quanxiaoha.airobot.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.airobot.domain.dos.ChatMessageDO;
/**
 * @Author: 犬小哈
 * @Date: 2025/8/11 11:36
 * @Version: v1.0.0
 * @Description: TODO
 **/
public interface ChatMessageMapper extends BaseMapper<ChatMessageDO> {

    //Page<ChatMessageDO>:一个存放 ChatMessageDO 数据的分页对象
    default Page<ChatMessageDO> selectPageList(Long current, Long size, String chatId){
        Page<ChatMessageDO> page = new Page<>(current, size);
        LambdaQueryWrapper<ChatMessageDO> wrapper = Wrappers.<ChatMessageDO>lambdaQuery()
                .eq(ChatMessageDO::getChatUuid, chatId) //等值查询，只查 chatUuid = chatId 的聊天记录。
                .orderByDesc(ChatMessageDO::getCreateTime);//按创建时间倒序，最新消息排在前面
        return selectPage(page, wrapper);//自动执行分页 SQL，查询数据 + 统计总条数，最终返回分页结果。
    }
}
