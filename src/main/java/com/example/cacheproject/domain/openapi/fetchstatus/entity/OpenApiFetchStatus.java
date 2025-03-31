package com.example.cacheproject.domain.openapi.fetchstatus.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "open_api_fetch_status")
public class OpenApiFetchStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int lastFetchedRow;  // 마지막으로 처리한 endRow 값을 저장

    @Column(nullable = false, updatable = false)
    private LocalDateTime updatedAt;  // 마지막 업데이트 시간

    @PrePersist
    public void onPrePersist() {
        this.updatedAt = LocalDateTime.now(); // 저장되기 전에 업데이트 시간 설정
    }
}
