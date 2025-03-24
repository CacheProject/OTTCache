package com.example.plusproject.collection.controller;

import com.example.plusproject.collection.service.CsvService;
import com.example.plusproject.openapi.service.OpenApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CollectionController {

    private final OpenApiService openApiService;
    private final CsvService csvService;

    // csv 파일을 1개 행씩 읽어서 db에 차례대로 insert
    @GetMapping("/collection")
    public String fetchCsvAndSaveToDatabase() {
        return csvService.readCsvAndSaveToDatabaseOneByOne();
    }

//    // csv 파일을 100개 행씩 읽어서 db에 차례대로 insert
//    @GetMapping("/collection-batch")
//    public String fetchCsvAndSaveInBatch() {
//        return csvService.readCsvAndSaveToDatabaseInBatch();
//    }

    // OpenAPI를 통해 데이터를 100개씩 db에 차례대로 insert
    @GetMapping("/collection-openapi")
    public ResponseEntity<String> fetchFromOpenApi() {
        String result = openApiService.fetchAndSaveOpenApiData();
        return ResponseEntity.ok(result);
    }
}
