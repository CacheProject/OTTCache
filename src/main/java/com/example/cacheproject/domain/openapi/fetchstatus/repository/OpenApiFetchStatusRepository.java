package com.example.cacheproject.domain.openapi.fetchstatus.repository;


import com.example.cacheproject.domain.openapi.fetchstatus.entity.OpenApiFetchStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpenApiFetchStatusRepository extends JpaRepository<OpenApiFetchStatus, Long> {
    OpenApiFetchStatus findTopByOrderByUpdatedAtDesc();  // 가장 최근에 업데이트된 데이터를 찾는 메서드
}
