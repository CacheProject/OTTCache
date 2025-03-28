package com.example.cacheproject.domain.store.dto.response;

import lombok.Getter;
import com.example.cacheproject.domain.store.entity.Store;

@Getter
public class StoreResponsDto {

    private final Long id;

    private final String storeName;

    private final String totalEvaluation;

    private final String storeStatus;

    private final String monitoringDate;

    public StoreResponsDto(Long id, String storeName, String totalEvaluation, String storeStatus, String monitoringDate) {
        this.id = id;
        this.storeName = storeName;
        this.totalEvaluation = totalEvaluation;
        this.storeStatus = storeStatus;
        this.monitoringDate = monitoringDate;
    }

    public static StoreResponsDto toDto(Store store) {
        return new StoreResponsDto(
                store.getId(),
                store.getStoreName(),
                store.getTotalEvaluation(),
                store.getStoreStatus(),
                store.getMonitoringDate()
        );
    }
}
