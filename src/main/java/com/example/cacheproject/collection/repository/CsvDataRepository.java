package com.example.cacheproject.collection.repository;

import com.example.cacheproject.collection.entity.CsvData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CsvDataRepository extends JpaRepository<CsvData, Long> {
}
