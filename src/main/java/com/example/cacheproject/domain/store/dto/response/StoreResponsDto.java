package com.example.cacheproject.domain.store.dto.response;

import lombok.Getter;
import com.example.cacheproject.domain.store.entity.Store;

@Getter
public class StoreResponsDto {

    private final Long id;

    private final String store_name;

    private final String total_evalution;

    private final String open_status;

    private final String monitoring_date;

    public StoreResponsDto(Long id, String store_name, String total_evalution, String open_status, String monitoring_date) {
        this.id = id;
        this.store_name = store_name;
        this.total_evalution = total_evalution;
        this.open_status = open_status;
        this.monitoring_date = monitoring_date;
    }

    public static StoreResponsDto toDto(Store store) {
        return new StoreResponsDto(
                store.getId(),
                store.getStore_name(),
                store.getTotal_evalution(),
                store.getOpen_status(),
                store.getMonitoring_date()
        );
    }
}
