package com.example.cacheproject.domain.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoreSummaryDto {

    private final String storeName;
    private final String email;
}
