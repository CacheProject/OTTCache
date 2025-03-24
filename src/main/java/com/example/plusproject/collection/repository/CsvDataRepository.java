package com.example.plusproject.collection.repository;

import com.example.plusproject.collection.entity.CsvData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CsvDataRepository extends JpaRepository<CsvData, Long> {
}
