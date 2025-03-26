package org.example.cacheproject.store.repository;

import org.example.cacheproject.store.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {

    @Query("SELECT s FROM Store s WHERE s.total_evalution = :score ORDER BY s.monitoring_date DESC LIMIT 10")
    List<Store> findTop10ByTotal_evalutionOrderByMonitoring_dateDesc(
            @Param("score") Integer score
    );

    @Query("SELECT s FROM Store s WHERE s.open_status = :status ORDER BY s.monitoring_date DESC LIMIT 10")
    List<Store> findTop10ByOpen_statusOrderByMonitoring_dateDesc(
            @Param("status") String status
    );

    @Query("SELECT s FROM Store s WHERE s.total_evalution = :score AND s.open_status = :status ORDER BY s.monitoring_date DESC LIMIT 10")
    List<Store> findTop10ByTotal_evalutionAndOpen_statusOrderByMonitoring_dateDesc(
            @Param("score") Integer score,
            @Param("status") String status
    );

    @Query("SELECT s FROM Store s WHERE s.total_evalution = :score AND s.open_status = :status")
    Page<Store> findAllStoresTotal_evalutionAndOpen_status(
            Pageable pageable,
            @Param("score") Integer score,
            @Param("status") String status
    );

}
