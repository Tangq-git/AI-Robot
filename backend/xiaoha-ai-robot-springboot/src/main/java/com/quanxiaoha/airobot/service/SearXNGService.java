package com.quanxiaoha.airobot.service;

import com.quanxiaoha.airobot.model.dto.SearchResultDTO;

import java.util.List;

public interface SearXNGService {
    List<SearchResultDTO> search(String query);
}
