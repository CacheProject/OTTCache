package com.example.cacheproject.domain.store.dto.response;

import com.example.cacheproject.domain.store.ScrollPaginationCollection;
import com.example.cacheproject.domain.store.entity.Store;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class GetStoreResponseDto {

    private static final long LAST_CURSOR = -1L;

    private List<StoreResponsDto> content = new ArrayList<>();
    private long totalElements;
    private long nextCursor;

    private GetStoreResponseDto(List<StoreResponsDto> content,long totalElements, long nextCursor) {
        this.content = content;
        this.totalElements = totalElements;
        this.nextCursor = nextCursor;
    }

    public static GetStoreResponseDto of(
            ScrollPaginationCollection<Store> storeScroll, long totalElements) {
        if (storeScroll.isLastScroll()) {
            return GetStoreResponseDto.newLastScroll(storeScroll.getCurrentScrollItems(),
                    totalElements);
        }
        return GetStoreResponseDto.newScrollHasNext(storeScroll.getCurrentScrollItems(),
                totalElements,
                storeScroll.getNextCursor().getId());
    }

    public static GetStoreResponseDto newLastScroll(List<Store> storesScroll, long totalElements) {
        return newScrollHasNext(storesScroll, totalElements, LAST_CURSOR);
    }

    public static GetStoreResponseDto newScrollHasNext(List<Store> storesScroll, long totalElements, long nextCursor) {
        return new GetStoreResponseDto(getContents(storesScroll), totalElements, nextCursor);
    }

    public static List<StoreResponsDto> getContents(List<Store> storesScroll) {
        return storesScroll.stream()
                .map(StoreResponsDto::toDto)
                .collect(Collectors.toList());
    }
}
