package com.quanxiaoha.airobot.model.vo.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AIResponse {
    //流式相应内容
    private  String v;

    //推理过程
    private String reasoning;
}
