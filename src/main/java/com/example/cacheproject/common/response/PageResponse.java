package com.example.cacheproject.common.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class PageResponse<T> implements Response<List<T>>, Serializable {
    private static final long serialVersionUID = 1L;

    private List<T> data;
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private long totalElements;

    // Jackson 역직렬화를 위한 생성자
    @JsonCreator
    public PageResponse(
            @JsonProperty("data") List<T> data,
            @JsonProperty("pageNumber") int pageNumber,
            @JsonProperty("pageSize") int pageSize,
            @JsonProperty("totalPages") int totalPages,
            @JsonProperty("totalElements") long totalElements
    ) {
        this.data = data;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }

    // 기본 생성자 (필요한 경우)
    public PageResponse() {}

    // Page로부터 생성하는 메서드
    public static <T> Response<List<T>> fromPage(Page<T> pageData) {
        return new PageResponse<>(
                pageData.getContent(),
                pageData.getNumber(),
                pageData.getSize(),
                pageData.getTotalPages(),
                pageData.getTotalElements()
        );
    }

    @Override
    public List<T> getData() {
        return this.data;
    }
}