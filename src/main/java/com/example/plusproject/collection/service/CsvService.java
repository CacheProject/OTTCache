package com.example.plusproject.collection.service;

import com.example.plusproject.collection.entity.CsvData;
import com.example.plusproject.collection.fetchstatus.entity.CsvDataFetchStatus;
import com.example.plusproject.collection.fetchstatus.repository.CsvDataFetchStatusRepository;
import com.example.plusproject.collection.repository.CsvDataRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class CsvService {

    private final CsvDataRepository csvDataRepository;
    private final CsvDataFetchStatusRepository csvDataFetchStatusRepository;

    @Value("${file.path}")
    private String filePath;

    private static final int BATCH_SIZE = 100;

    // 1개씩 읽어서 저장하는 로직
    @Transactional
    public String readCsvAndSaveToDatabaseOneByOne() {
        CsvDataFetchStatus fetchStatus = csvDataFetchStatusRepository.findById(1L).orElseGet(() -> {
            CsvDataFetchStatus newFetchStatus = new CsvDataFetchStatus();
            newFetchStatus.setLastFetchedRow(0);  // 기본값 0으로 설정
            return csvDataFetchStatusRepository.save(newFetchStatus);
        });

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new IllegalStateException("CSV 파일이 존재하지 않습니다: " + file.getAbsolutePath());
            }

            String absolutePath = file.getAbsolutePath();

            // CSVReader 초기화
            CSVReader csvReader = new CSVReader(new FileReader(absolutePath));
            csvReader.readNext(); // 첫 줄(헤더) 스킵

            String[] nextRecord = null;
            for (int i = 0; i <= fetchStatus.getLastFetchedRow(); i++) {
                nextRecord = csvReader.readNext();
                if (nextRecord == null) {
                    return "더 이상 읽을 CSV 데이터가 없습니다.";
                }
            }

            if (nextRecord != null) {
                CsvData csvData = mapCsvToEntity(nextRecord); // 엔티티로 변환
                csvDataRepository.save(csvData); // 데이터베이스에 저장

                // lastFetchedRow 갱신
                fetchStatus.setLastFetchedRow(fetchStatus.getLastFetchedRow() + 1);
                csvDataFetchStatusRepository.save(fetchStatus);

                return "CSV 파일에서 " + (fetchStatus.getLastFetchedRow()) + "번째 레코드가 성공적으로 데이터베이스에 저장되었습니다.";
            } else {
                return "더 이상 읽을 CSV 데이터가 없습니다.";
            }

        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
            return "CSV 파일 읽기 중 오류 발생: " + e.getMessage();
        } catch (IllegalStateException e) {
            return "파일 오류: " + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "알 수 없는 오류가 발생했습니다: " + e.getMessage();
        }
    }

    // csv -> 엔티티 변환
    private CsvData mapCsvToEntity(String[] record) {
        try {
            // record 배열 출력 (디버깅용)
            System.out.println("CSV Row Data: " + Arrays.toString(record));

            String companyName = record[0];  // 상호명
            String storeName = record[1];    // 쇼핑몰명
            String domainName = record[2];   // 도메인명
            String phoneNumber = record[3];  // 전화번호
            String operatorEmail = record[4]; // 운영자 이메일
            String companyAddress = record[8]; // 회사 주소

            int overallEvaluation = 0;
            try {
                overallEvaluation = Integer.parseInt(record[10]); // 전체평가
            } catch (NumberFormatException e) {
                overallEvaluation = 0; // 오류 발생 시 기본값으로 설정
            }

            String businessStatus = record[9]; // 영업형태

            // 필요한 데이터를 CsvData 엔티티로 변환하여 리턴
            return new CsvData(companyName, storeName, domainName, phoneNumber, operatorEmail,
                    companyAddress, overallEvaluation, businessStatus);
        } catch (Exception e) {
            // 예외가 발생하면 null 리턴 또는 에러 메시지를 로그로 출력
            System.err.println("CSV 처리 오류: " + e.getMessage());
            return null;
        }
    }
}
