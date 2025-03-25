package com.example.plusproject.collection.service;

import com.example.plusproject.collection.entity.CsvData;
import com.example.plusproject.collection.fetchstatus.entity.CsvDataFetchStatus;
import com.example.plusproject.collection.fetchstatus.repository.CsvDataFetchStatusRepository;
import com.example.plusproject.collection.repository.CsvDataRepository;
import com.example.plusproject.common.util.CsvReaderUtil;
import com.example.plusproject.common.util.DataConsistencyUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CsvService {

    @Value("${file.path}")
    private String filePath;

    private final CsvReaderUtil csvReaderUtil;
    private final DataConsistencyUtil dataConsistencyUtil;
    private final CsvDataFetchStatusRepository csvDataFetchStatusRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void readCsvAndSaveToDatabaseInBatch() {
        List<CsvData> batchList = new ArrayList<>();
        int batchSize = 100;
        int maxLimit = 10_000;
        int totalSaved = 0;

        // 마지막으로 삽입된 행을 가져오기
        CsvDataFetchStatus fetchStatus = csvDataFetchStatusRepository.findTopByOrderByUpdatedAtDesc();
        int startRow = 1;  // 기본값
        if (fetchStatus != null) {
            startRow = fetchStatus.getLastFetchedRow() + 1;
        }

        // CSV 데이터 읽기 및 DB에 배치 삽입
        for (CsvData entity : csvReaderUtil.readCsv(filePath)) {
            batchList.add(entity);
            if (batchList.size() >= batchSize) {
                batchInsert(batchList);

                if (dataConsistencyUtil.checkCsvDataConsistency(batchList)) { // ✅ 정합성 체크 추가
                    totalSaved += batchList.size();
                    batchList.clear();

                    // 마지막으로 처리된 행 번호 업데이트
                    CsvDataFetchStatus newFetchStatus = new CsvDataFetchStatus();
                    newFetchStatus.setLastFetchedRow(startRow + batchList.size() - 1);
                    csvDataFetchStatusRepository.save(newFetchStatus);

                    if (totalSaved >= maxLimit) {
                        break;
                    }
                } else {
                    System.out.println("⚠ 데이터 정합성 체크 실패! 배치 삽입 중단");
                    break;
                }
            }
        }

        if (!batchList.isEmpty()) {
            batchInsert(batchList);
        }
    }

    @Transactional
    public void batchInsert(List<CsvData> entities) {
        for (int i = 0; i < entities.size(); i++) {
            entityManager.persist(entities.get(i));
            if (i % 100 == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }
}