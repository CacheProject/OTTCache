package com.example.cacheproject.domain.store.service;

import com.example.cacheproject.domain.store.ScrollPaginationCollection;
import com.example.cacheproject.domain.store.dto.response.GetStoreResponseDto;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import com.example.cacheproject.domain.store.dto.response.StoreResponsDto;
import com.example.cacheproject.domain.store.entity.Store;
import com.example.cacheproject.domain.store.repository.StoreRepository;
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

    /* 전체평가 필터와 업소상태 필터(2개 필터 동시적용, 각각 1개씩 적용)를 적용 후 상위 10개만 조회(모니터링 날짜기준 내림차순 정렬)*/
    @Transactional(readOnly = true)
    public List<StoreResponsDto> findTopTenStores(Integer score, String status) {

        List<Store> storeList = new ArrayList<>();

        // 전체평가 필터만 사용
        if (score != null && (status == null || status.trim().isEmpty())) {
            storeList = storeRepository.findTop10ByTotal_evaluationOrderByMonitoring_dateDesc(score);
        } // 업소상태 필터만 사용
        else if (status != null && score == null) {
            storeList = storeRepository.findTop10ByOpen_statusOrderByMonitoring_dateDesc(status);
        }  // 전체평가 필터와 업소상태 필터 둘다 사용
        else {
            storeList = storeRepository.findTop10ByTotal_evaluationAndOpen_statusOrderByMonitoring_dateDesc(score, status);
        }

        List<StoreResponsDto> dtoList = new ArrayList<>();

        for (Store store : storeList) {
            StoreResponsDto dto = new StoreResponsDto(
                    store.getId(),
                    store.getStoreName(),
                    store.getTotalEvaluation(),
                    store.getStoreStatus(),
                    store.getMonitoringDate()
            );
            dtoList.add(dto);
        }

        return dtoList;
    }

    /* 전체평가 필터와 업소상태 필터(2개 필터 동시적용) - 페이징 조회 */
    @Transactional(readOnly = true)
    public Page<StoreResponsDto> findAllStores(int page, int size, Integer score, String status) {
        int adjustPage = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(adjustPage, size);

        Page<Store> storePage = storeRepository.findAllStoresTotal_evaluationAndOpen_status(pageable, score, status);

        List<StoreResponsDto> dtoList = storePage.getContent().stream()
                .map(StoreResponsDto::toDto)
                .toList();

        return new PageImpl<>(dtoList, pageable, storePage.getTotalElements());
    }

    // 커서 기반 페이지네이션 (필터 개별 및 동시 가능)
    @Transactional(readOnly = true)
    public GetStoreResponseDto getStoresByCursor(Integer score, String status, Long lastPageId, int size) {
       List<Store> storeList = storeRepository.findAllStoresByCursorTotal_evaluationAndOpen_status(score,status,lastPageId,size);

        ScrollPaginationCollection<Store> storeCursor = ScrollPaginationCollection.of(storeList, size);
        return GetStoreResponseDto.of(storeCursor, storeList.size());
    }
}
