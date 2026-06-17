package com.quanxiaoha.airobot.runner;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

/**
 * 项目启动自动加载测试文档，向量化存入PGVector
 */
@Component
public class InitEmbeddingIndexRunner implements ApplicationRunner {

    @Resource
    private VectorStore vectorStore;

    @Override
    public void run(ApplicationArguments args) {
        // 测试文档，和教程示例一致
        List<Document> documents = List.of(
                new Document("冰箱首次使用指南：拆箱后静置24小时再通电，调节温控器至4℃",
                        Map.of("章节", "安全须知", "版本", "2025")),
                new Document("节能技巧：避免频繁开关门，热食冷却后再放入，定期除霜可提升",
                        Map.of("章节", "节能维护")),
                new Document("故障代码手册：E1温度传感器异常，E2化霜故障，F1通讯错误。",
                        Map.of("章节", "故障处理", "紧急程度", "高"))
        );
        // 自动调用embedding模型向量化，写入pgvector表 t_vector_store
        vectorStore.add(documents);
        System.out.println("文档向量化完成，已存入PostgreSQL pgvector");
    }
}