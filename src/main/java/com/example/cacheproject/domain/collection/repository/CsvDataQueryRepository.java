package com.example.cacheproject.domain.collection.repository;

import com.example.cacheproject.domain.collection.entity.CsvData;

import java.util.List;

public interface CsvDataQueryRepository {

    List<CsvData> findAllCsvDataByCursor(Integer score, String status, Long lastPageId, int size);
}
