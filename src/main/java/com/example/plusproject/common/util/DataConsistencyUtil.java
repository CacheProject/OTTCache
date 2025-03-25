package com.example.plusproject.common.util;

import com.example.plusproject.collection.entity.CsvData;
import com.example.plusproject.collection.repository.CsvDataRepository;
import com.example.plusproject.openapi.entity.OpenApi;
import com.example.plusproject.openapi.repository.OpenApiRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataConsistencyUtil {

    private final CsvDataRepository csvDataRepository;
    private final OpenApiRepository openApiRepository;

    public boolean checkCsvDataConsistency(List<CsvData> batchList) {
        if (batchList.isEmpty()) {
            return true; // 저장할 데이터가 없는 경우 문제 없음
        }

        Random random = new Random();
        int sampleSize = Math.min(5, batchList.size()); // 최대 5개 랜덤 샘플링

        for (int i = 0; i < sampleSize; i++) {
            CsvData randomSample = batchList.get(random.nextInt(batchList.size()));

            // 데이터베이스에서 해당 ID의 CsvData 객체를 가져와 비교
            Optional<CsvData> dbRecord = csvDataRepository.findById(randomSample.getId());

            // DB 값 출력 (디버깅 용)
            if (dbRecord.isPresent()) {
                log.info("DB에서 조회한 CsvData: {}", dbRecord.get().toString());
            } else {
                log.warn("DB에서 해당 ID의 CsvData가 존재하지 않음: {}", randomSample.getId());
            }

            // CSV에서 뽑은 값 출력 (디버깅 용)
            log.info("CSV에서 뽑은 CsvData: {}", randomSample.toString());

            // CSV와 DB 값 비교
            if (dbRecord.isEmpty() || !dbRecord.get().equals(randomSample)) {
                log.error("정합성 체크 실패! CSV 데이터: {} DB 데이터: {}", randomSample.toString(), dbRecord.orElse(null));
                return false;  // 일치하지 않거나 존재하지 않으면 false 반환
            }
        }

        log.info("정합성 체크 성공: 샘플 {}개 데이터가 정상적으로 저장됨", sampleSize);
        return true;  // 정합성 체크가 성공하면 true 반환
    }


    public boolean checkOpenApiDataConsistency(List<OpenApi> batchList) {
        if (batchList.isEmpty()) {
            return true; // 저장할 데이터가 없는 경우 문제 없음
        }

        Random random = new Random();
        int sampleSize = Math.min(5, batchList.size()); // 최대 5개 랜덤 샘플링

        for (int i = 0; i < sampleSize; i++) {
            OpenApi randomSample = batchList.get(random.nextInt(batchList.size()));

            Optional<OpenApi> dbRecord = openApiRepository.findById(randomSample.getId());
            if (dbRecord.isEmpty() || !dbRecord.get().equals(randomSample)) {
                return false;
            }
        }
        return true;
    }
}
