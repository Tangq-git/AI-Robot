package com.quanxiaoha.airobot.controller;

import com.alibaba.dashscope.audio.ttsv2.SpeechSynthesisParam;
import com.alibaba.dashscope.audio.ttsv2.SpeechSynthesizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @Author: 犬小哈
 * @Date: 2025/5/29 12:32
 * @Version: v1.0.0
 * @Description: 文生音频
 **/
@RestController
@RequestMapping("/v11/ai")
@Slf4j
public class Text2AudioController {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @GetMapping("/text2audio")
    public String text2audio(@RequestParam(value = "prompt") String prompt) {

        SpeechSynthesisParam param = SpeechSynthesisParam.builder()
                .apiKey(apiKey) // 阿里百炼 API Key
                .model("cosyvoice-v2") // 模型名称
                .voice("longanran") // 音色
                .build();
        //阿里百炼 SDK 的语音合成调用工具类，传入上面组装好的参数，第二个参数传null代表用默认配置。
        SpeechSynthesizer synthesizer = new SpeechSynthesizer(param, null);
        //同步阻塞调用，传入待转语音的文本，等待云端处理完成，返回ByteBuffer格式的音频二进制数据。
        ByteBuffer audio = synthesizer.call(prompt);

        // 音频文件存储路径
        String path = "E:\\result-audio.mp3";
        File file = new File(path);

        //获取本次调用阿里接口的唯一请求 ID
        log.info("## requestId: {}", synthesizer.getLastRequestId());

        //文件输出流，负责往本地文件写二进制数据。
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(audio.array());//把 ByteBuffer 音频字节数组取出，写入文件
        } catch (IOException e) {//文件读写、路径权限、磁盘满等 IO 异常，打印日志
            log.error("", e);
        }
        return "success";

        //前端传文本 → 组装语音参数 → 同步调用语音模型拿到音频字节流 → 写入本地 MP3 文件 → 返回成功。
    }
}
