package com.example.cacheproject.domain.collection.repository;

import com.example.cacheproject.domain.collection.entity.CsvData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CsvDataRepository extends JpaRepository<CsvData, Long> {
}
