package com.example.cacheproject.common.util;

import com.example.cacheproject.collection.entity.CsvData;
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
                csvData.setBusinessStatus(nextLine[9]);

                csvDataList.add(csvData);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        return csvDataList;
    }
}
