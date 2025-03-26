package org.example.cacheproject.store.service;

import lombok.RequiredArgsConstructor;
import org.example.cacheproject.store.dto.response.StoreResponsDto;
import org.example.cacheproject.store.entity.Store;
import org.example.cacheproject.store.repository.StoreRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    @Transactional(readOnly = true)
    public List<StoreResponsDto> findTopTenStores(Integer score, String status) {

        List<Store> storeList = new ArrayList<>();

        // 전체평가 필터만 사용
        if (score != null && (status == null || status.trim().isEmpty())) {
            storeList = storeRepository.findTop10ByTotal_evalutionOrderByMonitoring_dateDesc(score);
        } // 업소상태 필터만 사용
        else if (status != null && score == null) {
            storeList = storeRepository.findTop10ByOpen_statusOrderByMonitoring_dateDesc(status);
        }  // 전체평가 필터와 업소상태 필터 둘다 사용
        else {
            storeList = storeRepository.findTop10ByTotal_evalutionAndOpen_statusOrderByMonitoring_dateDesc(score, status);
        }

        List<StoreResponsDto> dtoList = new ArrayList<>();

        for (Store store : storeList) {
            StoreResponsDto dto = new StoreResponsDto(
                    store.getId(),
                    store.getStore_name(),
                    store.getTotal_evalution(),
                    store.getOpen_status(),
                    store.getMonitoring_date()
            );
            dtoList.add(dto);
        }

        return dtoList;
    }

    @Transactional(readOnly = true)
    public Page<StoreResponsDto> findAllStores(int page, int size, Integer score, String status) {
        int adjustPage = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(adjustPage, size);

        Page<Store> storePage = storeRepository.findAllStoresTotal_evalutionAndOpen_status(pageable, score, status);

        List<StoreResponsDto> dtoList = storePage.getContent().stream()
                .map(StoreResponsDto::toDto)
                .toList();

        return new PageImpl<>(dtoList, pageable, storePage.getTotalElements());
    }
}
