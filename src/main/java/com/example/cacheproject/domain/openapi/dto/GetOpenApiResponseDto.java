package com.example.cacheproject.domain.openapi.dto;

import com.example.cacheproject.domain.collection.dto.CsvDataResponseDto;
import com.example.cacheproject.domain.collection.dto.GetCsvDataResponseDto;
import com.example.cacheproject.domain.collection.entity.CsvData;
import com.example.cacheproject.domain.openapi.entity.OpenApi;
import com.example.cacheproject.domain.store.ScrollPaginationCollection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class GetOpenApiResponseDto {

    private static final long LAST_CURSOR = -1L;

    private List<OpenApiResponseDto> content = new ArrayList<>();
    private long totalElements;
    private long nextCursor;

    private GetOpenApiResponseDto(List<OpenApiResponseDto> content, long totalElements, long nextCursor) {
        this.content = content;
        this.totalElements = totalElements;
        this.nextCursor = nextCursor;
    }

    public static GetOpenApiResponseDto of(
            ScrollPaginationCollection<OpenApi> openApiScroll, long totalElements) {
        OpenApi nextCursorApi = openApiScroll.getNextCursor();
        long nextCursor = (nextCursorApi != null) ? nextCursorApi.getId() : LAST_CURSOR;
        return new GetOpenApiResponseDto(getContents(openApiScroll.getCurrentScrollItems()), totalElements, nextCursor);
    }

    public static GetOpenApiResponseDto newLastScroll(List<OpenApi> openApiScroll, long totalElements) {
        return newScrollHasNext(openApiScroll, totalElements, LAST_CURSOR);
    }

    public static GetOpenApiResponseDto newScrollHasNext(List<OpenApi> openApiScroll, long totalElements, long nextCursor) {
        return new GetOpenApiResponseDto(getContents(openApiScroll), totalElements, nextCursor);
    }
    private static List<OpenApiResponseDto> getContents(List<OpenApi> openApiScroll) {
        return openApiScroll.stream()
                .map(OpenApiResponseDto::toDto)
                .collect(Collectors.toList());
    }
}
