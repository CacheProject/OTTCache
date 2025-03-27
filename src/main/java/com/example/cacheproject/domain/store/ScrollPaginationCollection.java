package com.example.cacheproject.domain.store;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ScrollPaginationCollection<T> {
    private final List<T> itemsWithNextCursor;
    private final int countPerScroll;

    public static <T> ScrollPaginationCollection<T> of(List<T> itemsWithNextCursor, int size) {
        return new ScrollPaginationCollection<>(itemsWithNextCursor,size);
    }

    public boolean isLastScroll() {
        return this.itemsWithNextCursor.size() < countPerScroll;
    }

    public List<T> getCurrentScrollItems() {
//        if (isLastScroll()) {
//            return itemsWithNextCursor;
//        }
//        return this.itemsWithNextCursor.subList(0, countPerScroll);
        return this.itemsWithNextCursor;
    }

    public T getNextCursor() {
        if (isLastScroll()) {
            return null;
        }
        return itemsWithNextCursor.get(itemsWithNextCursor.size() - 1);
    }
}
