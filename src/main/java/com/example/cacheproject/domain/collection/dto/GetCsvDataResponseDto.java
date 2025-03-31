package com.example.cacheproject.domain.collection.dto;

import com.example.cacheproject.domain.collection.entity.CsvData;
import com.example.cacheproject.domain.store.ScrollPaginationCollection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class GetCsvDataResponseDto {

    private static final long LAST_CURSOR = -1L;

    private List<CsvDataResponseDto> content = new ArrayList<>();
    private long totalElements;
    private long nextCursor;

    private GetCsvDataResponseDto(List<CsvDataResponseDto> content, long totalElements, long nextCursor) {
        this.content = content;
        this.totalElements = totalElements;
        this.nextCursor = nextCursor;
    }

    public static GetCsvDataResponseDto of(
            ScrollPaginationCollection<CsvData> csvDataScroll, long totalElements) {
        CsvData nextCursorData = csvDataScroll.getNextCursor();
        long nextCursor = (nextCursorData != null) ? nextCursorData.getId() : LAST_CURSOR;
        return new GetCsvDataResponseDto(getContents(csvDataScroll.getCurrentScrollItems()), totalElements, nextCursor);
    }

    public static GetCsvDataResponseDto newLastScroll(List<CsvData> csvDataScroll, long totalElements) {
        return newScrollHasNext(csvDataScroll, totalElements, LAST_CURSOR);
    }

    public static GetCsvDataResponseDto newScrollHasNext(List<CsvData> csvDataScroll, long totalElements, long nextCursor) {
        return new GetCsvDataResponseDto(getContents(csvDataScroll), totalElements, nextCursor);
    }
    private static List<CsvDataResponseDto> getContents(List<CsvData> csvDataScroll) {
        return csvDataScroll.stream()
                .map(CsvDataResponseDto::toDto)
                .collect(Collectors.toList());
    }
}
