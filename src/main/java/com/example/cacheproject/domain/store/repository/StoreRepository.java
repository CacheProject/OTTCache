package com.example.cacheproject.domain.store.repository;

import com.example.cacheproject.domain.store.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {

    // 전체평가 필터만 적용 후 상위 10개만 조회(모니터링 날짜기준 내림차순 정렬)하는 쿼리
    @Query("SELECT s FROM Store s WHERE s.total_evalution = :score ORDER BY s.monitoring_date DESC LIMIT 10")
    List<Store> findTop10ByTotal_evalutionOrderByMonitoring_dateDesc(
            @Param("score") Integer score
    );

    // 업소상태 필터만 적용 후 상위 10개만 조회(모니터링 날짜기준 내림차순 정렬)하는 쿼리
    @Query("SELECT s FROM Store s WHERE s.open_status = :status ORDER BY s.monitoring_date DESC LIMIT 10")
    List<Store> findTop10ByOpen_statusOrderByMonitoring_dateDesc(
            @Param("status") String status
    );

    // 전체평가 필터와 업소상태 필터를 동시에 적용 후 상위 10개만 조회(모니터링 날짜기준 내림차순 정렬)하는 쿼리
    @Query("SELECT s FROM Store s WHERE s.total_evalution = :score AND s.open_status = :status ORDER BY s.monitoring_date DESC LIMIT 10")
    List<Store> findTop10ByTotal_evalutionAndOpen_statusOrderByMonitoring_dateDesc(
            @Param("score") Integer score,
            @Param("status") String status
    );

    // 전체평가 필터와 업소상태 필터를 동시에 적용 후 조회하는 쿼리
    @Query("SELECT s FROM Store s WHERE s.total_evalution = :score AND s.open_status = :status")
    Page<Store> findAllStoresTotal_evalutionAndOpen_status(
            Pageable pageable,
            @Param("score") Integer score,
            @Param("status") String status
    );

    void deleteByUserId(Long userId);
    Optional<Store> findByUserId(Long userId);
}
