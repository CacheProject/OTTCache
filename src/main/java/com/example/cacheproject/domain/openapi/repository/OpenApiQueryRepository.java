package com.example.cacheproject.domain.openapi.repository;

import com.example.cacheproject.domain.openapi.entity.OpenApi;

import java.util.List;

public interface OpenApiQueryRepository {

    List<OpenApi> findAllOpenApiByCursor(Integer score, String status, Long lastPageId, int size);
}
