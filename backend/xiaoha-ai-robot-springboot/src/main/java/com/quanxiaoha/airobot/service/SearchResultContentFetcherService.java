package com.quanxiaoha.airobot.service;

import com.quanxiaoha.airobot.model.dto.SearchResultDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public interface SearchResultContentFetcherService {
    CompletableFuture<List<SearchResultDTO>> batchFetch(List<SearchResultDTO> searchResults, long timeout, TimeUnit unit);
}
