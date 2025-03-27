package com.example.cacheproject.domain.collection.fetchstatus.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "csv_data_fetch_status")
public class CsvDataFetchStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int lastFetchedRow = 0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onPrePersist() {
        this.updatedAt = LocalDateTime.now(); // 저장되기 전에 업데이트 시간 설정
    }
}
