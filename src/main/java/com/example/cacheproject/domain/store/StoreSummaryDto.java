package com.example.cacheproject.domain.store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class StoreSummaryDto {

    private final String storeName;
    private final String email;
}
