package com.quanxiaoha.airobot.model.vo.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 新建对话:出参
 * VO 就是专门给接口用的 “请求 / 响应模板”，用来规范前后端交互的数据格式，和数据库实体区分开。
 *数据库实体（如 ChatDO）里通常包含很多字段
 * （比如创建时间、更新时间、删除标记），但前端请求 / 响应不需要这些。
 * 用 VO 可以只传递必要字段，避免暴露敏感信息。
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewChatRspVO {

    private String summary;

    private String uuid;
}
