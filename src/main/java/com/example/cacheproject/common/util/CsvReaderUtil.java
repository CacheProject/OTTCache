package com.example.cacheproject.common.util;

import com.example.cacheproject.domain.collection.entity.CsvData;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CsvReaderUtil {

    public List<CsvData> readCsv(String filePath) {
        List<CsvData> csvDataList = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] nextLine;
            reader.readNext(); // 첫 번째 줄(헤더) 건너뛰기

            while ((nextLine = reader.readNext()) != null) {
                CsvData csvData = new CsvData();
                csvData.setCompanyName(nextLine[0]);
                csvData.setStoreName(nextLine[1]);
                csvData.setDomainName(nextLine[2]);
                csvData.setPhoneNumber(nextLine[3]);
                csvData.setOperatorEmail(nextLine[4]);
                csvData.setCompanyAddress(nextLine[8]);
                csvData.setStoreStatus(nextLine[9]);
                csvData.setMainProductCategory(nextLine[16]);
                csvData.setMonitoringDate(nextLine[31]);
                // totalEvaluation 값을 int로 변환하여 설정
                try {
                    int totalEvaluation = Integer.parseInt(nextLine[10]); // 해당 인덱스를 확인
                    csvData.setTotalEvaluation(totalEvaluation);
                } catch (NumberFormatException e) {
                    // 변환이 실패하면 기본값 설정 (예: 0)
                    csvData.setTotalEvaluation(0);
                }

                csvDataList.add(csvData);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        return csvDataList;
    }
}
