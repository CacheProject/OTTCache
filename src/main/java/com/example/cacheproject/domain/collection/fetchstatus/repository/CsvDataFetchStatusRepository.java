package com.example.cacheproject.domain.collection.fetchstatus.repository;

import com.example.cacheproject.domain.collection.fetchstatus.entity.CsvDataFetchStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CsvDataFetchStatusRepository extends JpaRepository<CsvDataFetchStatus, Long> {

    CsvDataFetchStatus findTopByOrderByUpdatedAtDesc();  // 가장 최근에 업데이트된 레코드 찾기
}
