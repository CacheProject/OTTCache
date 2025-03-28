package com.example.cacheproject.domain.collection.controller;

import com.example.cacheproject.domain.collection.dto.GetCsvDataResponseDto;
import com.example.cacheproject.domain.collection.service.CsvService;
import com.example.cacheproject.domain.openapi.service.OpenApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CollectionController {

    private final OpenApiService openApiService;
    private final CsvService csvService;

    // csv 파일을 100개 행씩 읽어서 db에 차례대로 insert
    @GetMapping("/collection-batch")
    public ResponseEntity<String> fetchCsvAndSaveInBatch() {
        String result = csvService.readCsvAndSaveToDatabaseInBatch();
        return ResponseEntity.ok(result);
    }

    // OpenAPI를 통해 데이터를 100개씩 db에 차례대로 insert
    @GetMapping("/collection-openapi")
    public ResponseEntity<String> fetchFromOpenApi() {
        String result = openApiService.fetchAndSaveOpenApiData();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/collection/paging")
    public ResponseEntity<GetCsvDataResponseDto> getCsvDateByCursor(
            @RequestParam(required = false) Integer score,
            @RequestParam(required = false) String status,
            @RequestParam Long lastPageId,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(csvService.getCsvDateByCursor(score, status, lastPageId, size));
    }
}
