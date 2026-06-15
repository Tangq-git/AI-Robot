package com.quanxiaoha.airobot.model.vo.chat;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: 犬小哈
 * @url: www.quanxiaoha.com
 * @date: 2023-09-15 14:07
 * @description: AI 聊天
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AiChatReqVO {

    @NotBlank(message = "用户消息不能为空")
    private String message;

    private String chatId;

    private Boolean networkSearch=false;

    @NotBlank(message = "调用的AI大模型名称不能为空")
    private String modelName;

    private Double temperature=0.7;
}
