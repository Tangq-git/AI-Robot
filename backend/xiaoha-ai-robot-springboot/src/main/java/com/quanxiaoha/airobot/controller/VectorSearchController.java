package com.quanxiaoha.airobot.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 向量相似度检索接口
 */
@RestController
@RequestMapping("/vector")
public class VectorSearchController {

    @Resource
    private VectorStore vectorStore;

    /**
     * 根据关键词检索相似文档
     * @param key 用户查询文本
     * @return 相似度top2文档
     */
    @GetMapping("/search")
    public List<Document> search(@RequestParam("key") String key) {
        SearchRequest request = SearchRequest.builder()
                .query(key)
                .topK(2)
                .build();
        return vectorStore.similaritySearch(request);
    }
}