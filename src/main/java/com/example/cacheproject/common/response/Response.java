package com.example.cacheproject.common.response;

import org.springframework.data.domain.Page;

import java.util.List;

public interface Response<T> {
    T getData();

    static <T> Response<T> of(T data) {
        return new DefaultResponse<>(data);
    }

    static <T> Response<List<T>> empty() {
        return new DefaultResponse<>(null);
    }

    static <T> Response<List<T>> fromPage(Page<T> pageData) {
        return PageResponse.fromPage(pageData);
    }
}