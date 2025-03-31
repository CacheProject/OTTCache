package com.example.cacheproject.domain.store.repository;

import com.example.cacheproject.domain.store.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface StoreQueryRepository {
    // 전체평가 필터만 적용 후 상위 10개만 조회(모니터링 날짜기준 내림차순 정렬)하는 쿼리
    List<Store> findTop10ByTotal_evaluationOrderByMonitoring_dateDesc (Integer score);
    // 업소상태 필터만 적용 후 상위 10개만 조회(모니터링 날짜기준 내림차순 정렬)하는 쿼리
    List<Store> findTop10ByOpen_statusOrderByMonitoring_dateDesc (String status);
    // 전체평가 필터와 업소상태 필터를 동시에 적용 후 상위 10개만 조회(모니터링 날짜기준 내림차순 정렬)하는 쿼리
    List<Store> findTop10ByTotal_evaluationAndOpen_statusOrderByMonitoring_dateDesc (Integer score, String status);
    // 전체평가 필터와 업소상태 필터를 동시에 적용 후 조회하는 쿼리
    Page<Store> findAllStoresTotal_evaluationAndOpen_status (Pageable pageable, Integer score, String status);
    // 전체평가 필터와 업소상태 펄터를 동시에 적용 후 cursor 기반 페이징네이션 쿼리
    List<Store> findAllStoresByCursorTotal_evaluationAndOpen_status(Integer score ,String status, Long lastPageId, int size);

    void deleteByUserId(Long userId);
//    Optional<Store> findByUserId(Long userId);
}
