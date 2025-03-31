package com.example.cacheproject.domain.collection.service;

import com.example.cacheproject.domain.collection.dto.GetCsvDataResponseDto;
import com.example.cacheproject.domain.collection.entity.CsvData;
import com.example.cacheproject.domain.collection.fetchstatus.entity.CsvDataFetchStatus;
import com.example.cacheproject.domain.collection.fetchstatus.repository.CsvDataFetchStatusRepository;
import com.example.cacheproject.common.util.BatchInsertUtil;
import com.example.cacheproject.common.util.CsvReaderUtil;
import com.example.cacheproject.common.util.DataConsistencyUtil;
import com.example.cacheproject.common.exception.DataIntegrityException;
import com.example.cacheproject.domain.collection.repository.CsvDataRepository;
import com.example.cacheproject.domain.store.ScrollPaginationCollection;
import com.example.cacheproject.domain.store.dto.response.GetStoreResponseDto;
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

    private final CsvDataRepository csvDataRepository;
    @Value("${file.path}")
    private String filePath; // CSV 파일 경로를 설정값에서 가져옴

    private final CsvReaderUtil csvReaderUtil;
    private final DataConsistencyUtil dataConsistencyUtil;
    private final CsvDataFetchStatusRepository csvDataFetchStatusRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // CSV 데이터를 읽고 db에 insert
    @Transactional
    public String readCsvAndSaveToDatabaseInBatch() {
        List<CsvData> batchList = new ArrayList<>();
        int batchSize = 100; // 배치 크기 설정
        int maxLimit = 10_000; // 한 번 API 호출에 최대 삽입 가능한 데이터 개수
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

                    // 마지막으로 처리된 행 번호 저장 및 업데이트
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

        // 남은 배치 단위 데이터 삽입
        if (!batchList.isEmpty()) {
            batchInsert(batchList);
        }

        return totalSaved + "개의 CSV 데이터가 성공적으로 삽입되었습니다.";
    }

    // CSV 데이터를 100개 단위로 db에 insert하는 메서드
    @Transactional
    public void batchInsert(List<CsvData> entities) {
        BatchInsertUtil.batchInsert(entityManager, entities);
    }

    // CSV insert 조회 메서드
    @Transactional(readOnly = true)
    public GetCsvDataResponseDto getCsvDateByCursor(Integer score, String status, Long lastPageId, int size) {
        List<CsvData> csvDataList = csvDataRepository.findAllCsvDataByCursor(score, status, lastPageId, size);

        ScrollPaginationCollection<CsvData> csvDataCursor = ScrollPaginationCollection.of(csvDataList, size);
        return GetCsvDataResponseDto.of(csvDataCursor, csvDataList.size());
    }
}