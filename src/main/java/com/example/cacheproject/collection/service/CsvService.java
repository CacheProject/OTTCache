package com.example.cacheproject.collection.service;

import com.example.cacheproject.collection.entity.CsvData;
import com.example.cacheproject.collection.fetchstatus.entity.CsvDataFetchStatus;
import com.example.cacheproject.collection.fetchstatus.repository.CsvDataFetchStatusRepository;
import com.example.cacheproject.common.util.CsvReaderUtil;
import com.example.cacheproject.common.util.DataConsistencyUtil;
import com.example.cacheproject.exception.DataIntegrityException;
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
    public String readCsvAndSaveToDatabaseInBatch() {
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

                // 정합성 체크
                if (dataConsistencyUtil.checkCsvDataConsistency(batchList)) {
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
                    throw new DataIntegrityException("데이터 정합성 체크 실패! 배치 삽입 중단");
                }
            }
        }

        // 남은 배치 데이터 삽입
        if (!batchList.isEmpty()) {
            batchInsert(batchList);
        }

        return totalSaved + "개의 CSV 데이터가 성공적으로 삽입되었습니다.";
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